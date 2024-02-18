/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model

import io.ktor.client.statement.*
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.createTempDirectory
import kotlin.time.Duration.Companion.milliseconds

object Utils {

    const val RESOURCES = "src/main/resources"
    const val FUNC_DEPLOYMENT = "function-deployment"
    const val FUNC_TEMPLATES = "function-templates"
    const val FUNC_DEFINITION = "function-definition"
    const val LANG_CONFIGS = "language-configs"
    const val LANG_SIGNATURES = "language-signatures-read-only"
    const val PROVIDER_CONFIGS = "provider-configs"
    const val TEMPORARY = "temporary"
    const val CONFIGS_FILE = "function-configs.json"
    const val ZIP_FILE = "function-source.zip"
    const val METRICS_RESOURCES = "src/test/resources/metrics"
    const val IMAGES = "images"

    fun openWebPage(url: String): Boolean {
        val uri: URI = URI.create(url)
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    // lfFormat: convert '\n\r' (windows new line char) to '\n'
    fun readFile(filePath: String, lfFormat: Boolean = false): String {
        val text: String = File(filePath).readText()
        return if (lfFormat) text.replace("\r", "") else text
    }

    fun readResFile(filePath: String, lfFormat: Boolean = false): String {
        val text: String = readResFileAsStream(filePath)?.readTextAndClose() ?: ""
        return if (lfFormat) text.replace("\r", "") else text
    }

    fun readResFileAsStream(filePath: String): InputStream? = Utils.javaClass.classLoader.getResourceAsStream(filePath)

    // Extension function
    private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }

    fun createFile(filePath: String, content: String) = File(filePath).writeText(content)

    fun createFileWithDirs(directories: String, fileName: String, content: String) {
        Files.createDirectories(Path.of(directories))
        createFile("$directories/$fileName", content)
    }

    fun createTempDir(baseDir: String): Path {
        val path = Path.of(baseDir)
        Files.createDirectories(path)
        return createTempDirectory(path, prefix = "quickfaas-tmp-")
    }

    fun copyFile(sourceFile: String, targetFile: String) = File(sourceFile).copyTo(
        File(targetFile), overwrite = true
    )

    fun copyDirRecursively(sourceDir: String, targetDir: String) = File("$RESOURCES/$sourceDir").copyRecursively(
        File("$RESOURCES/$targetDir"), overwrite = true
    )

    fun zipDirectory(sourceDir: String, targetZip: String, zipContentOnly: Boolean) {
        val fos = FileOutputStream(targetZip)
        val zipOut = ZipOutputStream(fos)
        val fileToZip = File(sourceDir)
        zipFile(fileToZip, fileToZip.name, zipOut, zipContentOnly)
        zipOut.close()
        fos.close()
    }

    // Recursive
    private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream, zipContentOnly: Boolean) {
        if (fileToZip.isHidden) return
        if (fileToZip.isDirectory) {
            val children = fileToZip.listFiles()
            if (zipContentOnly) {
                children!!.forEach { zipFile(it, it.name, zipOut, false) }
            } else {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(ZipEntry(fileName))
                    zipOut.closeEntry()
                } else {
                    zipOut.putNextEntry(ZipEntry("$fileName/"))
                    zipOut.closeEntry()
                }
                children!!.forEach { zipFile(it, "$fileName/${it.name}", zipOut, false) }
            }
            return
        }
        val fis = FileInputStream(fileToZip)
        zipOut.putNextEntry(ZipEntry(fileName))
        var length = 0
        val bytes = ByteArray(1024)
        while (length >= 0) {
            zipOut.write(bytes, 0, length)
            length = fis.read(bytes)
        }
        fis.close()
    }

    fun calculateHttpDuration(response: HttpResponse) =
        (response.responseTime.timestamp - response.requestTime.timestamp).milliseconds

}
