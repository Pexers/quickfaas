/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import view.styles.Modifiers

@Composable
fun dialog(enabled: Boolean, dialogContent: @Composable () -> Unit, dialogBelowContent: @Composable () -> Unit) {
    Box {
        dialogBelowContent()
        if (enabled) {
            Column(
                modifier = Modifier.fillMaxSize().background(color = Color.Black.copy(0.5f))
                    .clickable(enabled = false) {},
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(modifier = Modifier, shape = Modifiers.cardShape, elevation = Modifiers.cardElevation) {
                    dialogContent()
                }
            }
        }
    }
}

