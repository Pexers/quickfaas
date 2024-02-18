/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import view.styles.Modifiers

@Composable
fun selectionBar(selectedOption: String, options: List<String>, onChange: (idx: Int, value: String) -> Unit) {
    Card(modifier = Modifier.height(38.dp), shape = Modifiers.buttonShape, elevation = Modifiers.cardElevation) {
        Row {
            options.forEachIndexed { idx, value ->
                val isSelected = selectedOption == value
                Button(modifier = Modifier.width(85.dp).fillMaxHeight(),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) MaterialTheme.colors.primary else Color.White),
                    contentPadding = PaddingValues(2.dp),
                    onClick = {
                        if (!isSelected) {
                            onChange(idx, value)
                        }
                    }) {
                    Text(value, color = if (isSelected) Color.White else Color.Black)
                }
            }
        }
    }
}
