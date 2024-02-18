/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.specifics

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

interface CloudSpecifics {
    val preRequisites: @Composable (scope: CoroutineScope, onFinish: () -> Unit) -> Unit
}
