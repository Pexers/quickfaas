/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import model.CloudCompanion
import model.Utils
import model.Utils.METRICS_RESOURCES
import view.styles.TextStyles
import java.io.FileNotFoundException

private const val tokensFile = "metricsTokens.json"

@Composable
fun management(cloudProviders: Array<CloudCompanion>) {
    var tokensSaved by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("FaaS Management")
        Spacer(Modifier.height(50.dp))
        /*filledButton("Save tokens") {
            val tokensMap = readTokensFile()
            cloudProviders.forEach { cp ->
                if (cp.cloudAuth.isSignedIn()) {
                    tokensMap["${cp.shortName}_token"] = JsonPrimitive(cp.cloudAuth.session.token)
                }
            }
            Utils.createFile("$METRICS_RESOURCES/$tokensFile", JsonObject(tokensMap).toString())
            Utils.createFile("$METRICS_RESOURCES/.gitignore", tokensFile)
            tokensSaved = true
        }*/
        if (tokensSaved) Text("tokens saved", style = TextStyles.smallStyle)
    }
}

private fun readTokensFile(): MutableMap<String, JsonElement> {
    return try {
        Json.parseToJsonElement(
            Utils.readFile(
                "$METRICS_RESOURCES/$tokensFile",
                lfFormat = true
            )
        ).jsonObject.toMutableMap()
    } catch (e: FileNotFoundException) {
        mutableMapOf()
    }

}
