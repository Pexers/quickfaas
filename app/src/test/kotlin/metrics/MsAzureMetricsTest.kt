/*
 * Copyright (c) 5/11/2022, Pexers (https://github.com/Pexers)
 */

package metrics

import controller.General
import controller.HttpLogLevel
import io.ktor.util.date.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import metrics.common.CloudMetrics
import metrics.common.setDeploymentMsg
import model.MsAzureProvider
import model.projects.MsAzureProjectData
import model.requests.MsAzureRequests
import model.resources.functions.DeploymentTimeData
import model.resources.functions.MsAzureFunction
import model.resources.functions.runtimes.RuntimeVersion
import org.junit.jupiter.api.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
internal object MsAzureMetricsTest : CloudMetrics {
    override val nDeployments = 1
    override val nWarmStarts = 100
    override val faasResourceWarmTest = 1
    override val cloudProvider = MsAzureProvider()
    override val zipFile = "usecase3-msazure-non-agnostic.zip"
    override val project = "TFM"
    override val bucket = "bucket0general"
    override val location = "West Europe"
    override val runtimeVersion = RuntimeVersion.JAVA11
    override val requestBody = "{\"bucketName\":\"bucket1sources/quick-container\",\"blobSearch\":\"isel\"}"
    override val executionTimeMetric = "FunctionExecutionTimeMs"
    override val memoryUsageMetric = "FunctionExecutionUnits"

    private const val functionName = "search-blobs"
    private const val subscriptionId = "a7ceadd9-e071-4b48-b12a-ec6fb79ad360"
    private val dfISO8601: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")

    @BeforeTest
    fun setupCloudProviderSpecifics() {
        setupCloudProvider()
        (cloudProvider.project.projectData as MsAzureProjectData).subscriptionId = subscriptionId
        dfISO8601.timeZone = TimeZone.getTimeZone("UTC")
        General.HTTP_LOG_LEVEL = HttpLogLevel.NONE
    }

    @Test
    override fun deployFunctions() = try {
        runTest {
            cloudProvider.project.function.name = functionName
            var deploymentInfo: DeploymentTimeData
            for (i in 1..nDeployments) {
                val functionApp = faasResourceName(i)
                cloudProvider.project.let {
                    (it.function as MsAzureFunction).functionApp = functionApp
                    deploymentInfo = it.function.deployZip(getZipFilePath(zipFile), it.projectData, setDeploymentMsg)
                    putDeploymentInfo(functionApp, deploymentInfo)
                }
            }
        }
    } finally {
        saveDeploymentsInfo()
    }

    @Test
    fun deployInsightsApps() = runTest { // Needed for fetching metrics
        for (i in 1..nDeployments) {
            val functionApp = faasResourceName(i)
            deployInsightsApp(functionApp)
        }
    }

    @Test
    override fun triggerEachFunctionOnce() = try {
        runTest {
            var responseJson: JsonObject
            for (i in 1..nDeployments) {
                val functionApp = faasResourceName(i)
                (cloudProvider.project.function as MsAzureFunction).functionApp = functionApp
                responseJson = triggerFunction(functionName, functionApp, requestBody)
                putInternalMetrics(
                    functionApp, responseJson["internalExecTime"]!!.jsonPrimitive.double.milliseconds
                )
            }
        }
    } finally {
        printInternalMetrics()
    }

    @Test
    override fun triggerSingleFunctionManyTimes() = try {
        runTest {
            val functionApp = faasResourceName(faasResourceWarmTest)
            var responseJson: JsonObject
            (cloudProvider.project.function as MsAzureFunction).functionApp = functionApp
            // Trigger once to warm container
            triggerFunction(functionName, functionApp, requestBody, count = 1)
            Thread.sleep(60000)
            for (i in 1..nWarmStarts) {
                responseJson = triggerFunction(functionName, functionApp, requestBody, count = i)
                putInternalMetrics(
                    faasWarmInvocation(i, functionApp),
                    responseJson["internalExecTime"]!!.jsonPrimitive.double.milliseconds
                )
                Thread.sleep(10000)
            }
        }
    } finally {
        printInternalMetrics()
    }

    @Test
    override fun fetchDeploymentMetrics() = runTest {
        val timestamp = getTimestamp(80)
        var functionApp: String
        var deploymentEnd: String
        var changes: JsonArray
        for (i in 1..nDeployments) {
            functionApp = faasResourceName(i)
            println("Fetching '$functionApp'...")
            changes = MsAzureRequests.getActivityLogs(subscriptionId, project, functionApp, timestamp)
            run loop@{
                changes.forEach {
                    if (it.jsonObject["changeType"]!!.jsonPrimitive.content == "Create") {
                        deploymentEnd = it.jsonObject["afterSnapshot"]!!.jsonObject["timestamp"]!!.jsonPrimitive.content
                        putDeploymentInfo(
                            functionApp, DeploymentTimeData(deploymentEndDate = GMTDate(parseToUtc(deploymentEnd).time))
                        )
                        return@loop  // Break loop
                    }
                }
            }
        }
        printDeployments()
    }

    @Test
    override fun fetchColdStartMetrics() = runTest {
        val timestamp = getTimestamp(50)
        var functionApp: String
        for (i in 1..nDeployments) {
            functionApp = faasResourceName(i)
            println("Fetching '$functionApp'...")
            val insightsApp = getInsightsAppInfo(functionApp)
            val executionTimes = MsAzureRequests.queryInsightsAppMetrics(
                appId = insightsApp.first,
                apiKey = insightsApp.second,
                timestamp = timestamp,
                query = "requests| project timestamp, customDimensions['$executionTimeMetric']| sort by timestamp asc"
            )
            var executionTime = 0.0
            if (executionTimes.isNotEmpty()) {
                val executionTimeValue = executionTimes[0].jsonArray[1].jsonPrimitive.content
                if (executionTimeValue != "null") {
                    executionTime = executionTimeValue.toDouble()
                    putExecutionTime(functionApp, executionTime.milliseconds)
                }
            }

            val funcExecUnits = MsAzureRequests.fetchInsightsAppMetrics(
                subscriptionId, project, functionApp, memoryUsageMetric, timestamp, "aggregation=Average"
            ).mapNotNull { unit ->
                val execUnit = unit.jsonObject["average"]!!.jsonPrimitive.double
                if (execUnit > 0) execUnit
                else null
            }
            if (funcExecUnits.size == 1) {
                val memoryUsage = calculateMemoryUsage(funcExecUnits[0], executionTime)
                putMemoryUsage(functionApp, memoryUsage.toFloat())
            } else if (funcExecUnits.size > 1) println("Above one")
        }
        printMetrics()
    }

    @Test
    override fun fetchWarmStartMetrics() = runTest {
        val timestamp = getTimestamp(37)
        val functionApp = faasResourceName(faasResourceWarmTest)
        val insightsApp = getInsightsAppInfo(functionApp)
        val executionTimes = MsAzureRequests.queryInsightsAppMetrics(
            appId = insightsApp.first,
            apiKey = insightsApp.second,
            timestamp = timestamp,
            query = "requests| project timestamp, customDimensions['$executionTimeMetric']| sort by timestamp asc"
        ).mapNotNull { time ->
            val execTimeValue = time.jsonArray[1].jsonPrimitive.content
            if (execTimeValue != "null") execTimeValue.toDouble()
            else null
        }
        executionTimes.forEachIndexed { idx, time ->
            putExecutionTime(faasWarmInvocation(idx + 1, functionApp), time.milliseconds)
        }

        val funcExecUnits = MsAzureRequests.fetchInsightsAppMetrics(
            subscriptionId, project, functionApp, memoryUsageMetric, timestamp, "aggregation=Average"
        ).mapNotNull { unit ->
            println(unit)
            val execUnit = unit.jsonObject["average"]!!.jsonPrimitive.double
            if (execUnit > 0) execUnit
            else null
        }
        val avgExecUnit = funcExecUnits.sumOf { it } / funcExecUnits.size
        val avgExecTime = executionTimes.sumOf { it } / executionTimes.size
        val avgMemUsage = calculateMemoryUsage(avgExecUnit, avgExecTime)
        putMemoryUsage(faasWarmInvocation(1, functionApp), avgMemUsage.toFloat())

        printMetrics()
    }

    private suspend fun deployInsightsApp(functionApp: String) {
        val insightsApp = insightsAppName(functionApp)
        println("Deploying '$insightsApp'...")
        val appId = MsAzureRequests.createAppInsights(
            subscriptionId, project, functionApp, insightsApp, location
        )
        val apiKey = MsAzureRequests.createInsightsAppApiKey(
            subscriptionId, project, insightsApp, keyName = "${insightsAppName(functionApp)}-key"
        )
        if (apiKey != null) {  // Save newly created API key of the insights app
            val tokens = readTokens().toMutableMap()
            val insightsObj = JsonObject(mapOf("appId" to JsonPrimitive(appId), "apiKey" to JsonPrimitive(apiKey)))
            tokens[insightsAppName(functionApp)] = insightsObj
            saveTokens(JsonObject(tokens).toString())
        }
    }

    // Limit time
    private fun getTimestamp(minutes: Int) =  // start_time/end_time
        "${dfISO8601.format(Date(Date().time - (minutes * 60 * 1000)))}/${dfISO8601.format(Date())}"

    private fun getInsightsAppInfo(functionApp: String): Pair<String, String> {
        val insightsObj = readTokens()[insightsAppName(functionApp)]!!.jsonObject
        return Pair(insightsObj["appId"]!!.jsonPrimitive.content, insightsObj["apiKey"]!!.jsonPrimitive.content)
    }

    private fun insightsAppName(functionApp: String) = "$functionApp-insights"

    // memory_usage(MB) = func_exec_unit(MB-ms) / execution_time(ms)
    private fun calculateMemoryUsage(functionExecutionUnit: Double, executionTime: Double) =
        functionExecutionUnit.div(executionTime)

}
