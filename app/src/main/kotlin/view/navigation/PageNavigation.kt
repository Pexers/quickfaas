/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import view.common.filledButton
import view.common.outlinedButton
import view.styles.Modifiers.NavButtonsModifier

private typealias Page = @Composable () -> Unit

class PageNavigation {
    lateinit var navigate: (page: Page) -> Unit

    @Composable
    fun firstPage(page: Page) {
        var currentPage by remember { mutableStateOf(page) }
        navigate = { currentPage = it }
        currentPage()
    }
}

@Composable
fun navigationButtons(
    pageNav: PageNavigation,
    previousPage: Page? = null,
    nextPage: Page? = null,
    previousEnabler: Boolean = true,
    nextEnabler: Boolean = true
) {
    Row(
        modifier = NavButtonsModifier, horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Top,
    ) {
        if (previousPage != null) {
            outlinedButton("Previous", enabled = previousEnabler) { pageNav.navigate { previousPage() } }
        }
        if (nextPage != null) {
            Spacer(Modifier.width(20.dp))
            filledButton("Next", enabled = nextEnabler) { pageNav.navigate { nextPage() } }
        }
    }
}
