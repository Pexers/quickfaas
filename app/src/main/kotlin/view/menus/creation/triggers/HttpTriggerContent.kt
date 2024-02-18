/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.triggers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.resources.functions.triggers.HttpTrigger
import view.styles.TextStyles

private fun nextEnabler() = true

@Composable
fun httpTriggerContent(trigger: HttpTrigger, isReady: (nextEnabler: Boolean) -> Unit) {
    isReady(nextEnabler())
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            "Work in progress",
            modifier = Modifier.background(Color.LightGray).padding(5.dp),
            style = TextStyles.codeStyle,
            color = Color.DarkGray
        )
    }
}
