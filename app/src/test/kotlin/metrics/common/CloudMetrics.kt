/*
 * Copyright (c) 5/16/2022, Pexers (https://github.com/Pexers)
 */

package metrics.common

import controller.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.*
import kotlinx.serialization.json.*
import model.CloudProvider
import model.Utils
import model.Utils.METRICS_RESOURCES
import model.resources.functions.DeploymentTimeData
import model.resources.functions.runtimes.RuntimeVersion
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

private val metricsMap = mutableMapOf<String, MetricsData>()
private val deploymentsInfo = mutableMapOf<String, DeploymentTimeData>()
private const val tokensFile = "metricsTokens.json"
private val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
val setDeploymentMsg: (msg: String) -> Unit = { msg -> println("$msg...") }

interface CloudMetrics {
    val nDeployments: Int  // nColdStarts
    val nWarmStarts: Int
    val faasResourceWarmTest: Int
    val cloudProvider: CloudProvider
    val zipFile: String
    val project: String
    val bucket: String
    val location: String
    val runtimeVersion: RuntimeVersion
    val requestBody: String
    val executionTimeMetric: String
    val memoryUsageMetric: String

    // Metrics tests
    fun deployFunctions()
    fun triggerEachFunctionOnce()
    fun triggerSingleFunctionManyTimes()
    fun fetchDeploymentMetrics()
    fun fetchColdStartMetrics()
    fun fetchWarmStartMetrics()

    fun faasResourceName(idx: Int) = "metrics-test-$idx"
    fun faasWarmInvocation(idx: Int, faasResource: String) = "$idx. $faasResource"
    fun getZipFilePath(zipFile: String) = "$METRICS_RESOURCES/function-sources/$zipFile"
    private fun getTokensPath() = "$METRICS_RESOURCES/$tokensFile"
    private fun getMetricsDataPath() = "$METRICS_RESOURCES/data/${cloudProvider.companion.shortName}-executions.txt"
    private fun getInternalMetricsDataPath() =
        "$METRICS_RESOURCES/data/${cloudProvider.companion.shortName}-executions-internal.txt"

    private fun getDeploymentsDataPath() =
        "$METRICS_RESOURCES/data/${cloudProvider.companion.shortName}-deployments.txt"

    private fun getDeploymentsInfoPath() =
        "$METRICS_RESOURCES/data/${cloudProvider.companion.shortName}-deployments-info.json"

    fun readTokens() = Json.parseToJsonElement(Utils.readFile(getTokensPath())).jsonObject
    fun readDeploymentsInfo() = Json.parseToJsonElement(Utils.readFile(getDeploymentsInfoPath())).jsonObject

    fun getOAuthToken(): String {
        try {
            val tokens = readTokens()
            val cpKey = cloudProvider.companion.shortName + "_token"
            if (!tokens.containsKey(cpKey)) throw JsonConvertException("Token not found. Please use '<cpShortName>_token' as the key.")
            return tokens[cpKey]!!.jsonPrimitive.content
        } catch (e: FileNotFoundException) {
            throw FileNotFoundException("Missing tokens file. Please save tokens before running metrics tests.")
        }
    }

    fun saveTokens(content: String) {
        Utils.createFile(getTokensPath(), content)
        Utils.createFile("$METRICS_RESOURCES/.gitignore", tokensFile)
    }

    fun saveDeploymentsInfo() {
        val jsonMap = mutableMapOf<String, JsonObject>()
        deploymentsInfo.forEach {
            jsonMap[it.key] = JsonObject(
                mapOf(
                    "zipUploadTime" to JsonPrimitive(it.value.zipUploadTime.inWholeMilliseconds),
                    "deploymentStart" to JsonPrimitive(it.value.deploymentStartDate.timestamp),
                )
            )
        }
        Utils.createFile(getDeploymentsInfoPath(), JsonObject(jsonMap).toString())
    }

    fun parseToUtc(date: String): Date = utcFormat.parse(date)

    fun setupCloudProvider() {
        cloudProvider.companion.cloudRequests.setBearerToken(getOAuthToken())
        cloudProvider.project.let {
            it.projectData.name = project
            it.function.bucket.bucketData.name = bucket
            it.function.runtimeVersion = runtimeVersion
            it.function.location = location
        }
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun triggerFunction(
        functionName: String, resourceName: String, bodyJson: String, count: Int = -1
    ): JsonObject {
        cloudProvider.project.function.name = functionName
        val responseJson =
            httpClient.post(cloudProvider.project.function.getTriggerUrl(cloudProvider.project.projectData).second) {
                contentType(ContentType.Application.Json)
                setBody(Json.parseToJsonElement(bodyJson))
            }.body<JsonObject>()
        val countStr = if (count == -1) "" else "$count. "
        println("$countStr'$resourceName'\t| Response body: $responseJson")
        return responseJson
    }

    fun putDeploymentInfo(resourceName: String, deploymentInfo: DeploymentTimeData) {
        deploymentsInfo[resourceName] = deploymentInfo
    }

    fun putExecutionTime(resourceName: String, executionTime: Duration) =
        if (metricsMap.containsKey(resourceName)) metricsMap[resourceName]!!.executionTime = executionTime
        else metricsMap[resourceName] = MetricsData(executionTime = executionTime)

    fun putMemoryUsage(resourceName: String, memoryUsage: Float) =
        if (metricsMap.containsKey(resourceName)) metricsMap[resourceName]!!.memoryUsage = memoryUsage
        else metricsMap[resourceName] = MetricsData(memoryUsage = memoryUsage)

    fun putInternalMetrics(resourceName: String, internalExecTime: Duration) {
        metricsMap[resourceName] = MetricsData(internalExecTime = internalExecTime)
    }

    fun printDeployments() {
        printDivider()
        if (deploymentsInfo.isEmpty()) {
            println("No deployments found.")
            return
        }
        var sumDeploymentTimes: Duration = ZERO
        val deploymentsInfo = readDeploymentsInfo()
        var deploymentInfo: JsonObject
        var deploymentTime: Duration
        var countDeploymentTimes = 0
        getDataWriter(getDeploymentsDataPath()).use { out ->
            out.write("FaaS resource\tTotal time (ms)\tResource deployment time (ms)\tZIP upload time (ms)\n")
            metrics.common.deploymentsInfo.forEach {
                if (!deploymentsInfo.containsKey(it.key)) return@forEach  // Continue
                deploymentInfo = deploymentsInfo[it.key]!!.jsonObject
                val deploymentStart = deploymentInfo["deploymentStart"]!!.jsonPrimitive.double
                if (deploymentStart == 0.0) return@forEach  // Continue
                val deploymentEnd = it.value.deploymentEndDate.timestamp
                val zipUploadTime = deploymentInfo["zipUploadTime"]!!.jsonPrimitive.double
                val resDeploymentTime = deploymentEnd - deploymentStart
                deploymentTime = (resDeploymentTime + zipUploadTime).milliseconds
                sumDeploymentTimes = sumDeploymentTimes.plus(deploymentTime)
                println("${it.key}\t| Deployment time: $deploymentTime")
                out.write("${it.key}\t${deploymentTime.inWholeMilliseconds}\t${resDeploymentTime.milliseconds.inWholeMilliseconds}\t${zipUploadTime.milliseconds.inWholeMilliseconds}\n")
                countDeploymentTimes++
            }
        }
        val average = sumDeploymentTimes.div(if (countDeploymentTimes == 0) 1 else countDeploymentTimes)
        println("-> Average deployment time: $average")
        printDivider()
    }

    fun printMetrics() {
        printDivider()
        if (metricsMap.isEmpty()) {
            println("No metrics found.")
            return
        }
        var executionTime: Duration
        var memoryUsage: Float
        var sumExecTimes: Duration = ZERO
        var sumMemUsages = 0f
        var countExecTimes = 0
        var countMemUsages = 0
        getDataWriter(getMetricsDataPath()).use { out ->
            out.write("FaaS resource\tExecution time (ns)\tMemory usage (MB)\n")
            metricsMap.forEach {
                if (it.value.executionTime == null) executionTime = ZERO
                else {
                    executionTime = it.value.executionTime!!
                    countExecTimes++
                }
                if (it.value.memoryUsage == null) memoryUsage = 0f
                else {
                    memoryUsage = it.value.memoryUsage!!
                    countMemUsages++
                }
                sumExecTimes += executionTime
                sumMemUsages += memoryUsage
                println("${it.key}\t| Execution time: $executionTime   \t| Memory usage: ${memoryUsage}MB")
                out.write("${it.key}\t${executionTime.inWholeNanoseconds}\t$memoryUsage\n")
            }
        }
        val averageExecTime = sumExecTimes.div(if (countExecTimes == 0) 1 else countExecTimes)
        val averageMemUsage = sumMemUsages / countMemUsages
        println("-> Average execution time: $averageExecTime   \t| Average memory usage: ${averageMemUsage}MB")
        printDivider()
    }

    fun printInternalMetrics() {
        printDivider()
        if (metricsMap.isEmpty()) {
            println("No metrics found.")
            return
        }
        var internalExecTime: Duration
        var sumInternalExecTimes: Duration = ZERO
        getDataWriter(getInternalMetricsDataPath()).use { out ->
            out.write("FaaS resource\tInternal execution time (ms)\n")
            metricsMap.forEach {
                internalExecTime = it.value.internalExecTime!!
                sumInternalExecTimes += internalExecTime
                println("${it.key}\t| Internal execution time: $internalExecTime")
                out.write("${it.key}\t${internalExecTime.inWholeMilliseconds}\n")
            }
        }
        val averageExecTime = sumInternalExecTimes.div(metricsMap.size)
        println("-> Average execution time: $averageExecTime")
        printDivider()
    }

    private fun printDivider() = println("----------")

    private fun getDataWriter(filePath: String) = File(filePath).bufferedWriter()

}
