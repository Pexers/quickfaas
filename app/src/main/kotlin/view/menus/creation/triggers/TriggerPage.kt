/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.triggers

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.CloudProvider
import model.resources.functions.triggers.Trigger
import view.common.selectionBar
import view.menus.creation.configuration.configurationPage
import view.menus.creation.development.hookFunctionPage
import view.navigation.PageNavigation
import view.navigation.navigationButtons
import view.styles.Modifiers.PageModifier

@Composable
fun triggerPage(cp: CloudProvider, pageNav: PageNavigation) {
    val proj = cp.project
    var selectedTrigger by remember { mutableStateOf(proj.function.trigger) }
    var triggerFinished by remember { mutableStateOf(false) }

    Column(PageModifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            selectionBar(
                selectedOption = selectedTrigger.name,
                options = proj.function.triggers.map { trigger: Trigger -> trigger.name },
                onChange = { idx, _ ->
                    proj.function.setTrigger(idx)
                    selectedTrigger = proj.function.trigger
                    triggerFinished = false
                })
        }
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
            selectedTrigger.triggerContent(proj) { nextEnabler ->
                triggerFinished = nextEnabler
            }
        }
    }
    navigationButtons(
        pageNav,
        previousPage = { configurationPage(cp, pageNav) },
        nextPage = { hookFunctionPage(cp, pageNav) },
        nextEnabler = triggerFinished
    )
}
