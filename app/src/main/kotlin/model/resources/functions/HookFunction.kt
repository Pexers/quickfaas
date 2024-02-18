/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions

import model.resources.functions.runtimes.Language

class HookFunction {

    var definition = ""
    var dependencies = ""
    var dependsFile = ""
    val defaultConfigs =
        "{\n  \"resources\": [\n    {\n      \"id\": \"\",\n      \"type\": \"\",\n      \"properties\": {}\n    }\n  ]\n}"
    var configurations = defaultConfigs
    var configsFile = ""
    lateinit var templateFile: String

    fun setDependencies(depends: String, fileName: String) {
        dependencies = depends
        dependsFile = fileName
    }

    fun setConfigurations(configs: String, fileName: String) {
        configurations = configs
        configsFile = fileName
    }

    fun getAgnosticSignature(language: Language, triggerShortName: String): String {
        val langConfigs = language.getConfigurations()
        val triggerConfig = langConfigs.triggers.first { it.name == triggerShortName }

        templateFile = triggerConfig.templateFile

        // @formatter:off
        var triggerParams = ""
        triggerConfig.parameters.forEach { parameter ->
            triggerParams += "${langConfigs.parameterSyntax
                .replace("<type>", parameter.type)
                .replace("<name>", parameter.name)}, "
        }
        triggerParams = triggerParams.dropLast(2)  // Drop last two characters
        var triggerPackages = ""
        triggerConfig.packages.forEach {
            triggerPackages += langConfigs.packageSyntax.replace("<package>", it) + "\n"
        }
        definition = language.getSignature()
            .replace("<packages>", triggerPackages)
            .replace("<parameters>", triggerParams)
            .replace("<definition>", triggerConfig.defaultDefinition)
        // @formatter:on
        return definition
    }

}
