/*
 * Copyright (c) 5/11/2022, Pexers (https://github.com/Pexers)
 */

package metrics

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.monitoring.v3.MetricServiceClient
import com.google.cloud.monitoring.v3.MetricServiceSettings
import com.google.monitoring.v3.ListTimeSeriesRequest
import com.google.monitoring.v3.Point
import com.google.monitoring.v3.ProjectName
import com.google.monitoring.v3.TimeInterval
import com.google.protobuf.util.Timestamps
import controller.General
import controller.HttpLogLevel
import io.ktor.client.call.*
import io.ktor.util.date.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive
import metrics.common.CloudMetrics
import metrics.common.setDeploymentMsg
import model.GcpProvider
import model.projects.GcpProjectData
import model.requests.GcpRequests
import model.resources.functions.DeploymentTimeData
import model.resources.functions.runtimes.RuntimeVersion
import org.junit.jupiter.api.Test
import java.math.RoundingMode
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds

@OptIn(ExperimentalCoroutinesApi::class)
internal object GcpMetricsTest : CloudMetrics {
    override val nDeployments = 3
    override val nWarmStarts = 600
    override val faasResourceWarmTest = 40
    override val cloudProvider = GcpProvider()
    override val zipFile = "usecase3-gcp-agnostic.zip"
    override val project = "meic-tfm1-2021-2022"
    override val bucket = "gcf-sources-827942895545-europe-west1"
    override val location = "europe-west1"
    override val runtimeVersion = RuntimeVersion.JAVA11
    override val requestBody = "{\"bucketName\":\"bucket1sources\",\"blobSearch\":\"isel\"}"
    override val executionTimeMetric = "execution_times"
    override val memoryUsageMetric = "user_memory_bytes"

    private lateinit var metricsClient: MetricServiceClient

    @BeforeTest
    fun setupCloudProviderSpecifics() {
        setupCloudProvider()
        cloudProvider.project.let {
            (it.projectData as GcpProjectData).projectId = project
            // Function's entry point needs to be established
            val langConfig = runtimeVersion.runtime.language.getConfigurations()
            it.function.hookFunction.templateFile =
                langConfig.triggers.first { trigger -> trigger.name == it.function.trigger.shortName }.templateFile
        }
        val credential = GoogleCredentials.create(AccessToken(getOAuthToken(), null))
        val metricsSettings =
            MetricServiceSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credential))
                .build()
        metricsClient = MetricServiceClient.create(metricsSettings)
        General.HTTP_LOG_LEVEL = HttpLogLevel.NONE
    }

    @Test
    override fun deployFunctions() = try {
        runTest {
            var deploymentInfo: DeploymentTimeData
            for (i in 1..nDeployments) {
                val functionName = faasResourceName(i)
                cloudProvider.project.let {
                    it.function.name = functionName
                    deploymentInfo = it.function.deployZip(getZipFilePath(zipFile), it.projectData, setDeploymentMsg)
                    putDeploymentInfo(functionName, deploymentInfo)
                }
            }
        }
    } finally {
        saveDeploymentsInfo()
    }

    @Test
    override fun triggerEachFunctionOnce() = try {
        runTest {
            var responseJson: JsonObject
            for (i in 1..nDeployments) {
                val functionName = faasResourceName(i)
                responseJson =
                    triggerFunction(resourceName = functionName, functionName = functionName, bodyJson = requestBody)
                putInternalMetrics(functionName, responseJson["internalExecTime"]!!.jsonPrimitive.double.milliseconds)
            }
        }
    } finally {
        printInternalMetrics()
    }

    @Test
    override fun triggerSingleFunctionManyTimes() = try {
        runTest {
            val functionName = faasResourceName(faasResourceWarmTest)
            var responseJson: JsonObject
            // Trigger once to warm container
            triggerFunction(resourceName = functionName, functionName = functionName, bodyJson = requestBody, count = 1)
            Thread.sleep(60000)
            for (i in 1..nWarmStarts) {
                responseJson = triggerFunction(
                    resourceName = functionName, functionName = functionName, bodyJson = requestBody, count = i
                )
                putInternalMetrics(
                    faasWarmInvocation(i, functionName),
                    responseJson["internalExecTime"]!!.jsonPrimitive.double.milliseconds
                )
                Thread.sleep(10000)  // Increases probability of metrics being registered
            }
        }
    } finally {
        printInternalMetrics()
    }

    @Test
    override fun fetchDeploymentMetrics() = runTest {
        var functionName: String
        var deploymentEnd: String
        for (i in 1..nDeployments) {
            functionName = faasResourceName(i)
            deploymentEnd = GcpRequests.getCloudFunction(project, location, functionName)
                .body<JsonObject>()["updateTime"]?.jsonPrimitive?.content ?: ""
            if (deploymentEnd.isNotEmpty()) putDeploymentInfo(
                functionName, DeploymentTimeData(deploymentEndDate = GMTDate(parseToUtc(deploymentEnd).time))
            )
        }
        printDeployments()
    }

    @Test
    override fun fetchColdStartMetrics() = runTest {
        val timestamp = getTimestamp(20)
        val reqExecTimes = getMetricsRequest(executionTimeMetric, timestamp)
        metricsClient.listTimeSeries(reqExecTimes).iterateAll().forEach {
            val functionName = it.resource.labelsMap["function_name"]!!
            val point = it.pointsList.firstOrNull { p -> p.value.distributionValue.mean > 0 }
            if (point != null) putExecutionTime(functionName, point.value.distributionValue.mean.nanoseconds)
        }
        val reqMemUsage = getMetricsRequest(memoryUsageMetric, timestamp)
        var functionName: String
        metricsClient.listTimeSeries(reqMemUsage).iterateAll().forEach {
            functionName = it.resource.labelsMap["function_name"]!!
            val point = it.pointsList.firstOrNull { p -> p.value.distributionValue.mean > 0 }
            if (point != null) {
                val roundBig =
                    point.value.distributionValue.mean.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
                putMemoryUsage(functionName, (roundBig / 1E6).toFloat())
            }
        }
        printMetrics()
    }

    @Test
    override fun fetchWarmStartMetrics() {
        val timestamp = getTimestamp(130)
        val functionName = faasResourceName(faasResourceWarmTest)
        val reqExecTimes = getMetricsRequest(executionTimeMetric, timestamp)
        var intervalPoints = listOf<Point>()
        metricsClient.listTimeSeries(reqExecTimes).iterateAll().forEach {
            if (it.resource.labelsMap["function_name"]!! != functionName) return@forEach  // Continue
            val points = it.pointsList.filter { p -> p.value.distributionValue.mean > 0 }
            intervalPoints = if (it.pointsCount > nWarmStarts) points.dropLast(it.pointsCount - nWarmStarts) else points
        }
        //TODO: Try to sort data by timestamp instead of reversing
        intervalPoints = intervalPoints.reversed()  // Older points first
        intervalPoints.forEachIndexed { idx, point ->
            putExecutionTime(faasWarmInvocation(idx + 1, functionName), point.value.distributionValue.mean.nanoseconds)
        }
        intervalPoints = listOf()
        val reqMemUsage = getMetricsRequest(memoryUsageMetric, timestamp)
        metricsClient.listTimeSeries(reqMemUsage).iterateAll().forEach {
            if (it.resource.labelsMap["function_name"]!! != functionName) return@forEach  // Continue
            val points = it.pointsList.filter { p -> p.value.distributionValue.mean > 0 }
            intervalPoints = if (it.pointsCount > nWarmStarts) points.dropLast(it.pointsCount - nWarmStarts) else points
        }
        intervalPoints = intervalPoints.reversed()  // Older points first
        intervalPoints.forEachIndexed { idx, point ->
            val memoryUsage = point.value.distributionValue.mean
            putMemoryUsage(faasWarmInvocation(idx + 1, functionName), formatMemoryUsage(memoryUsage))
        }
        printMetrics()
    }

    private fun getMetricsRequest(filter: String, timestamp: TimeInterval): ListTimeSeriesRequest =
        ListTimeSeriesRequest.newBuilder().setName(ProjectName.of(project).toString()).setInterval(timestamp)
            .setView(ListTimeSeriesRequest.TimeSeriesView.FULL).setFilter(filter)
            .setFilter("metric.type=\"cloudfunctions.googleapis.com/function/$filter\"").build()

    // Limit time
    private fun getTimestamp(minutes: Int) = TimeInterval.newBuilder()
        .setStartTime(Timestamps.fromMillis(System.currentTimeMillis() - ((60 * minutes) * 1000)))
        .setEndTime(Timestamps.fromMillis(System.currentTimeMillis())).build()

    private fun formatMemoryUsage(memoryUsage: Double): Float =
        (memoryUsage.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble() / 1E6).toFloat()

}
