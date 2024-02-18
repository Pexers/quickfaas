/*
 * Copyright (c) 5/11/2022, Pexers (https://github.com/Pexers)
 */

package other

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.CloudCompanion
import model.GcpProvider
import model.MsAzureProvider
import model.resources.functions.HookFunction
import model.resources.functions.runtimes.RuntimeVersion
import model.resources.functions.triggers.HttpTrigger
import view.menus.creation.development.hookFunctionPage
import view.navigation.MenuData
import view.navigation.PageNavigation
import view.navigation.menuNavigation
import view.styles.AppColors

/** This is not really a test ... */

private val cloudProviders: Array<CloudCompanion> = arrayOf(MsAzureProvider, GcpProvider)

fun main() {
    application { Window(onCloseRequest = ::exitApplication) { app() } }
}

@Composable
private fun devTesting(pageNav: PageNavigation) {
    val cp = GcpProvider()
    cp.project.projectData.name = "MEIC-TFM1-2021-2022"
    cp.project.function.let {
        it.hookFunction = HookFunction()
        it.name = "my-faas-test"
        it.location = "europe-west1"
        it.trigger = HttpTrigger()
        it.runtimeVersion = RuntimeVersion.JAVA11
    }
    hookFunctionPage(cp, pageNav)
}

@Composable
private fun app() {
    MaterialTheme(colors = AppColors.MainColors) {
        menuNavigation(
            arrayOf(MenuData(title = "Testing", icon = "debug.png") {
                val pageNav = PageNavigation()
                pageNav.firstPage { devTesting(pageNav) }
            }, MenuData(title = "Testing2", icon = "debug.png") {},
                MenuData(title = "Testing3", icon = "debug.png") {})
        )
    }
}

