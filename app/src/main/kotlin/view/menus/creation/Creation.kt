/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.CloudCompanion
import model.Utils
import model.authentication.TokenListeners
import view.common.iconButton
import view.menus.creation.authencation.cloudCard
import view.menus.creation.configuration.configurationPage
import view.navigation.PageNavigation

@Composable
fun creation(cloudProviders: Array<CloudCompanion>, newAppWindow: () -> Unit, windowId: Int) {
    var tokenSender by remember { mutableStateOf("") }
    var ccSelected by remember { mutableStateOf<CloudCompanion?>(null) }

    TokenListeners.addTokenListener(windowId) { cpShortName -> tokenSender = cpShortName }
    val onChangeCC: (cc: CloudCompanion) -> Unit = { ccSelected = it }
    val pageNav = PageNavigation()

    if (ccSelected == null) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                cloudProviders.forEach {
                    cloudCard(it, isSignedIn = it.cloudAuth.isSignedIn() || tokenSender == it.shortName, onChangeCC)
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) { iconButton("New window", "${Utils.IMAGES}/tabs.png") { newAppWindow() } }
        }
    } else {
        // Cloud provider color line
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier.fillMaxWidth(0.7f).height(3.dp).background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, ccSelected!!.cloudViewData.colors.primary, Color.Transparent)
                    )
                )
            )
        }
        pageNav.firstPage { configurationPage(ccSelected!!.newCloudProvider(), pageNav) }
    }
}
