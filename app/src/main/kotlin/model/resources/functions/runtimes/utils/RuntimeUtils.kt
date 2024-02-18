/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.runtimes.utils

import model.Utils
import model.resources.functions.runtimes.Runtime

interface RuntimeUtils {
    val runtime: Runtime

    fun readTemplateFile(fileName: String, sourcesDir: String) =
        Utils.readResFile(filePath = "${runtime.templatesDir}/$sourcesDir/$fileName")

    fun createFileInTmp(fileName: String, content: String, tmpDirName: String) =
        Utils.createFileWithDirs(directories = "${runtime.tmpDir}/$tmpDirName", fileName, content)

    fun copySourceFileToTmp(fileName: String, sourcesDir: String, tmpDirName: String) =
        createFileInTmp(fileName, readTemplateFile(fileName, sourcesDir), tmpDirName)

}