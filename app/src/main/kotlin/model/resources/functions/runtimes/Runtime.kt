/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.runtimes

import model.Utils.FUNC_DEPLOYMENT
import model.Utils.FUNC_TEMPLATES
import model.Utils.TEMPORARY

enum class Runtime(
    val runtimeName: String,
    val shortName: String,
    val language: Language,
    val dependsSyntax: String,
    val dependsFileExtension: String
) {
    JAVA(
        runtimeName = "Java",
        shortName = "java",
        language = Language.JAVA,
        dependsSyntax = "<dependencies>\n    \n</dependencies>",
        dependsFileExtension = ".xml"
    ),
    NODEJS(
        runtimeName = "Node.js",
        shortName = "nodejs",
        language = Language.JAVASCRIPT,
        dependsSyntax = "",
        dependsFileExtension = ".json"
    );

    val templatesDir = "${FUNC_TEMPLATES}/${this.shortName}"
    val deploymentDir = "$FUNC_DEPLOYMENT/${this.shortName}"
    val tmpDir = "$deploymentDir/$TEMPORARY"

}
