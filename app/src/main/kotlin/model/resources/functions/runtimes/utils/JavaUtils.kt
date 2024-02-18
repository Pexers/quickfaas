/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.runtimes.utils

import model.Utils
import model.resources.functions.runtimes.Runtime
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.apache.maven.shared.invoker.InvocationRequest
import java.io.File

object JavaUtils : RuntimeUtils {
    override val runtime = Runtime.JAVA
    private const val mavenHome = "apache-maven-3.8.6"
    private const val pomFile = "pom.xml"

    fun readPom(sourcesDir: String) = Utils.readResFile(filePath = "${runtime.templatesDir}/$sourcesDir/$pomFile")

    fun createPom(pomContent: String, tmpDirName: String) =
        Utils.createFile(filePath = "${runtime.tmpDir}/$tmpDirName/$pomFile", pomContent)

    fun setPomProperties(pomContent: String, properties: Array<Pair<String, String>>): String {
        var pomWithProps = pomContent
        if (properties.isNotEmpty()) properties.forEach {
            pomWithProps = pomWithProps.replace("<${it.first}>", "<${it.first}>${it.second}")
        }
        return pomWithProps
    }

    fun setPomDependencies(pomContent: String, dependencies: String): String =
        if (dependencies.isNotBlank()) pomContent.replace("<dependencies>", dependencies.replace("</dependencies>", ""))
        else pomContent

    fun createJavaFile(fileName: String, content: String, tmpDirName: String) =
        Utils.createFileWithDirs(directories = "${runtime.tmpDir}/$tmpDirName/src/main/java", fileName, content)

    fun mavenBuild(tmpDirName: String) {
        val request: InvocationRequest = DefaultInvocationRequest()
        request.mavenHome = File("${runtime.deploymentDir}/$mavenHome")
        request.baseDirectory = File("${runtime.tmpDir}/$tmpDirName")
        request.goals = listOf("package")
        val result = DefaultInvoker().execute(request)
        if (result.exitCode != 0) {
            throw Exception(result.executionException)
        }
    }

    fun zipBuildSources(tmpDirName: String, sourceDir: String) {
        Utils.zipDirectory(
            sourceDir = "${runtime.tmpDir}/$tmpDirName/target/$sourceDir",
            targetZip = "${runtime.tmpDir}/$tmpDirName/${Utils.ZIP_FILE}",
            zipContentOnly = true
        )
    }

}
