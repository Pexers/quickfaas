/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun information() {
    Text("Information", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
}

