/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.common

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun circularProgress(enabled: Boolean, modifier: Modifier = Modifier) {
    if (enabled) CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colors.primary,
        strokeWidth = 3.dp
    )
}
