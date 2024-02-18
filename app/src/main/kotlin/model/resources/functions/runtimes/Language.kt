/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.runtimes

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.Utils

enum class Language(
    private var configurations: LanguageConfigsData? = null,  // Lazy loading applied
    private var signature: String = "",  // Lazy loading applied
    private val configsFile: String
) {
    JAVA(configsFile = "java-configs.json"), JAVASCRIPT(configsFile = "javascript-configs.json");

    fun getConfigurations(): LanguageConfigsData {
        if (configurations == null) {
            val langConfigJson = Utils.readResFile("${Utils.FUNC_DEFINITION}/${Utils.LANG_CONFIGS}/$configsFile")
            configurations = Json.decodeFromString(langConfigJson)
        }
        return configurations!!
    }

    fun getSignature(): String = signature.ifEmpty {
        signature = Utils.readResFile(
            filePath = "${Utils.FUNC_DEFINITION}/${Utils.LANG_SIGNATURES}/${getConfigurations().signatureFile}",
            lfFormat = true
        )
        signature
    }
}

@Serializable
data class LanguageConfigsData(
    val signatureFile: String,
    val packageSyntax: String,
    val parameterSyntax: String,
    val triggers: List<TriggerConfigsData>
)

@Serializable
data class TriggerConfigsData(
    val name: String,
    val templateFile: String,
    val defaultDefinition: String,
    val parameters: List<ParameterConfigsData>,
    val packages: List<String>
)

@Serializable
data class ParameterConfigsData(val name: String, val type: String)
