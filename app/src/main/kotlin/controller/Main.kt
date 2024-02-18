/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package controller

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.GcpProvider
import model.MsAzureProvider
import view.menus.creation.creation
import view.menus.information
import view.menus.management
import view.menus.settings
import view.navigation.MenuData
import view.navigation.menuNavigation
import view.styles.AppColors

private val cloudProviders = arrayOf(MsAzureProvider, GcpProvider)
private lateinit var newAppWindow: () -> Unit

fun main() = runBlocking {
    launch(Dispatchers.IO) { startHttpServer(cloudProviders) } // Start HTTP server asynchronously
    application {
        var windows by remember { mutableStateOf(1) }
        newAppWindow = { windows += 1 }
        for (i in 1..windows) appWindow(i - 1)
    }
}

@Composable
fun appWindow(windowId: Int) {
    var closed by remember { mutableStateOf(false) }
    if (!closed) {
        Window(
            title = if (windowId == 0) "QuickFaaS" else "QuickFaaS ($windowId)",
            onCloseRequest = { closed = true }) { app(windowId) }
    }
}

@Composable
fun app(windowId: Int) {
    MaterialTheme(colors = AppColors.MainColors) {
        menuNavigation(
            arrayOf(MenuData(title = "FaaS Creation", icon = "function.png") {
                creation(cloudProviders, newAppWindow, windowId)
            },
                MenuData(title = "FaaS Management", icon = "bullet-list.png") { management(cloudProviders) },
                MenuData(title = "Settings", icon = "settings.png") { settings() },
                MenuData(title = "Information", icon = "info.png") { information() })
        )
    }
}
