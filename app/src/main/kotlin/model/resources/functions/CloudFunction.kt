/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions

import controller.General.AUTO_DELETE_FUNC_ZIP
import model.Utils
import model.projects.ProjectData
import model.resources.buckets.CloudBucket
import model.resources.functions.runtimes.Runtime
import model.resources.functions.runtimes.RuntimeVersion
import model.resources.functions.runtimes.scripts.CloudBuildScripts
import model.resources.functions.triggers.Trigger
import java.io.File

interface CloudFunction {
    var name: String
    var hookFunction: HookFunction
    var buildScripts: CloudBuildScripts
    val bucket: CloudBucket
    val locations: List<String>
    var location: String
    val triggers: List<Trigger>
    var trigger: Trigger
    val runtimes: Array<RuntimeVersion>
    var runtimeVersion: RuntimeVersion?

    fun buildAndZip(cpShortName: String, setDeploymentMsg: (msg: String) -> Unit): File {
        var tmpDir = File("")
        try {
            tmpDir = Utils.createTempDir(runtimeVersion!!.runtime.tmpDir).toFile()
            val sourcesDir = "$cpShortName/${trigger.shortName}"  // e.g.: {runtime}/gcp/http
            when (runtimeVersion!!.runtime) {
                Runtime.JAVA -> {
                    setDeploymentMsg("Building maven project")
                    buildScripts.javaBuildScript(this, sourcesDir, tmpDir.name)
                }
                else -> throw NotImplementedError()
            }
        } catch (e: Exception) {
            if (AUTO_DELETE_FUNC_ZIP && tmpDir.path.isNotEmpty()) tmpDir.deleteRecursively()
            throw e
        }
        return tmpDir
    }

    suspend fun deployZip(
        zipFilePath: String, projData: ProjectData, setDeploymentMsg: (msg: String) -> Unit
    ): DeploymentTimeData

    fun getEntryPoint(): String
    fun getTriggerUrl(projData: ProjectData): Pair<String, String>

    fun setTrigger(triggerIdx: Int) {
        trigger = triggers[triggerIdx]
        runtimeVersion = null
        hookFunction.definition = ""
        hookFunction.dependencies = ""
    }

    fun setRuntimeVersion(runtimeIdx: Int) {
        val selectedRuntimeVersion = runtimes[runtimeIdx]
        runtimeVersion = selectedRuntimeVersion
        hookFunction.dependencies = selectedRuntimeVersion.runtime.dependsSyntax
    }
}
