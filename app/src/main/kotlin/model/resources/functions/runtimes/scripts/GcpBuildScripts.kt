/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.runtimes.scripts

import model.Utils
import model.resources.functions.CloudFunction
import model.resources.functions.runtimes.utils.JavaUtils

object GcpBuildScripts : CloudBuildScripts {

    override fun javaBuildScript(func: CloudFunction, sourcesDir: String, tmpDirName: String) {
        val signatureFile = func.runtimeVersion!!.runtime.language.getConfigurations().signatureFile
        val entryPoint = func.getEntryPoint()
        JavaUtils.let {
            var pomContent = it.readPom(sourcesDir)
            pomContent = it.setPomDependencies(pomContent, func.hookFunction.dependencies)
            var templateContent = it.readTemplateFile(entryPoint, sourcesDir)
            templateContent = templateContent.replace("<configs_file>", Utils.CONFIGS_FILE)
            it.createPom(pomContent, tmpDirName)
            it.createJavaFile(entryPoint, templateContent, tmpDirName)
            it.createJavaFile(signatureFile, func.hookFunction.definition, tmpDirName)
            it.createFileInTmp(Utils.CONFIGS_FILE, func.hookFunction.configurations, "$tmpDirName/src/main/resources")
            it.mavenBuild(tmpDirName)
            it.zipBuildSources(tmpDirName, "gcp-function")
        }
    }

    override fun nodeJsBuildScript() {
        TODO("Not yet implemented")
    }
}