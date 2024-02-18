/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.runtimes.scripts

import model.Utils
import model.resources.functions.CloudFunction
import model.resources.functions.MsAzureFunction
import model.resources.functions.runtimes.utils.JavaUtils
import model.resources.functions.triggers.StorageTrigger

object MsAzureBuildScripts : CloudBuildScripts {

    override fun javaBuildScript(func: CloudFunction, sourcesDir: String, tmpDirName: String) {
        func as MsAzureFunction
        func.setFunctionApp()
        val signatureFile = func.runtimeVersion!!.runtime.language.getConfigurations().signatureFile
        val entryPoint = func.getEntryPoint()
        JavaUtils.let {
            var pomContent = it.readPom(sourcesDir)
            pomContent = it.setPomDependencies(pomContent, func.hookFunction.dependencies)
            pomContent = it.setPomProperties(pomContent, arrayOf(Pair("functionAppName", func.functionApp)))
            var templateContent = it.readTemplateFile(entryPoint, sourcesDir)
            templateContent = templateContent.replace("<name>", func.name).replace("<configs_file>", Utils.CONFIGS_FILE)
            when (func.trigger) {
                is StorageTrigger -> {
                    val storageTrigger = func.trigger as StorageTrigger
                    templateContent = templateContent.replace("<storage_account>", storageTrigger.bucketData!!.name)
                        .replace("<container>", func.container)
                }
            }
            it.copySourceFileToTmp("host.json", sourcesDir, tmpDirName)
            it.createPom(pomContent, tmpDirName)
            it.createJavaFile(entryPoint, templateContent, tmpDirName)
            it.createJavaFile(signatureFile, func.hookFunction.definition, tmpDirName)
            it.createFileInTmp(Utils.CONFIGS_FILE, func.hookFunction.configurations, "$tmpDirName/src/main/resources")
            it.mavenBuild(tmpDirName)
            it.zipBuildSources(tmpDirName, "azure-functions/${func.functionApp}")
        }
    }

    override fun nodeJsBuildScript() {
        TODO("Not yet implemented")
    }
}