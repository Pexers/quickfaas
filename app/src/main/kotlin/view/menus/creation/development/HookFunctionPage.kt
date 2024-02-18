/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.development

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.CloudProvider
import model.Utils
import model.resources.functions.runtimes.RuntimeVersion
import view.common.circularProgress
import view.common.dropDownField
import view.common.filePickerField
import view.menus.creation.deployment.deploymentPage
import view.menus.creation.triggers.triggerPage
import view.navigation.PageNavigation
import view.navigation.navigationButtons
import view.styles.Modifiers.PageModifier

private val codeEditorWidth = 900.dp
private val codeEditorHeight = 490.dp
private val codeEditorPadding = PaddingValues(start = 25.dp, end = 25.dp)

@Composable
fun hookFunctionPage(cp: CloudProvider, pageNav: PageNavigation) {
    val projFunc = cp.project.function
    var runtimeSelected by remember { mutableStateOf(projFunc.runtimeVersion != null) }
    var definition by remember { mutableStateOf(projFunc.hookFunction.definition) }
    var dependencies by remember { mutableStateOf(projFunc.hookFunction.dependencies) }
    var configurations by remember { mutableStateOf(projFunc.hookFunction.configurations) }
    var progressEnabler by remember { mutableStateOf(false) }
    var dependsEnabler by remember { mutableStateOf(false) }
    var configsEnabler by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val codeEnabler = runtimeSelected && !progressEnabler

    codeEditorDialog(dependencies, dependsEnabler, {
        dependencies = projFunc.hookFunction.dependencies
        dependsEnabler = false
    }, {
        projFunc.hookFunction.dependencies = dependencies
        dependsEnabler = false
    }, {
        dependencies = it
    }) {
        codeEditorDialog(configurations, configsEnabler, {
            configurations = projFunc.hookFunction.configurations
            configsEnabler = false
        }, {
            projFunc.hookFunction.configurations = configurations
            configsEnabler = false
        }, {
            configurations = it
        }) {
            Column {
                Column(PageModifier) {
                    Row(
                        modifier = Modifier.fillMaxHeight(0.25f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Row(
                            Modifier.fillMaxSize().padding(codeEditorPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.5f).padding(end = 45.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                dropDownField(title = "Runtime",
                                    defaultOption = getRuntimeOption(projFunc.runtimeVersion),
                                    options = projFunc.runtimes.map { rv -> getRuntimeOption(rv) },
                                    hint = "Select a runtime",
                                    fieldTitleWidth = null,
                                    onChange = { idx, _ ->
                                        projFunc.setRuntimeVersion(idx)
                                        progressEnabler = true
                                        runtimeSelected = true
                                        projFunc.runtimeVersion!!.let {
                                            dependencies = it.runtime.dependsSyntax
                                            scope.launch {
                                                withContext(Dispatchers.IO) {
                                                    definition = projFunc.hookFunction.getAgnosticSignature(
                                                        it.runtime.language, projFunc.trigger.shortName
                                                    )
                                                }
                                                progressEnabler = false
                                            }
                                        }
                                    })
                            }
                            Column(Modifier.fillMaxWidth()) {
                                val fieldTitleWidth = 120.dp
                                filePickerField(title = "Dependencies",
                                    defaultFileName = projFunc.hookFunction.dependsFile,
                                    extension = if (codeEnabler) projFunc.runtimeVersion!!.runtime.dependsFileExtension else "",
                                    enabled = codeEnabler,
                                    fieldTitleWidth = fieldTitleWidth,
                                    onClickEdit = { dependsEnabler = true }) { directory, file ->
                                    if (file.isEmpty()) {
                                        projFunc.hookFunction.setDependencies(
                                            projFunc.runtimeVersion!!.runtime.dependsSyntax, ""
                                        )
                                    } else {
                                        val depends = Utils.readFile(directory + file, lfFormat = true)
                                        projFunc.hookFunction.setDependencies(depends, file)
                                    }
                                    dependencies = projFunc.hookFunction.dependencies
                                }
                                Spacer(Modifier.height(20.dp))
                                filePickerField(title = "Configurations",
                                    defaultFileName = projFunc.hookFunction.configsFile,
                                    extension = ".json",
                                    enabled = codeEnabler,
                                    fieldTitleWidth = fieldTitleWidth,
                                    onClickEdit = { configsEnabler = true }) { directory, file ->
                                    if (file.isEmpty()) {
                                        projFunc.hookFunction.setConfigurations(
                                            projFunc.hookFunction.defaultConfigs, ""
                                        )
                                    } else {
                                        val configs = Utils.readFile(directory + file, lfFormat = true)
                                        projFunc.hookFunction.setConfigurations(configs, file)
                                    }
                                    configurations = projFunc.hookFunction.configurations
                                }
                            }
                        }

                    }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.padding(codeEditorPadding).padding(bottom = 10.dp)) {
                            codeWindow(
                                if (progressEnabler) "" else definition,
                                enabled = codeEnabler,
                                modifier = Modifier.width(codeEditorWidth).height(codeEditorHeight),
                                disabledMsg = "Select a runtime"
                            ) {
                                projFunc.hookFunction.definition = it
                                definition = it
                            }
                            Row(
                                modifier = Modifier.width(codeEditorWidth).height(codeEditorHeight),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) { circularProgress(enabled = progressEnabler) }
                        }
                    }
                }
                navigationButtons(
                    pageNav,
                    previousPage = { triggerPage(cp, pageNav) },
                    nextPage = { deploymentPage(cp, pageNav) },
                    nextEnabler = nextEnabler(runtimeSelected)
                )
            }
        }
    }
}

private fun getRuntimeOption(rv: RuntimeVersion?) = if (rv == null) "" else "${rv.runtime.runtimeName} ${rv.version}"

private fun nextEnabler(runtimeSelected: Boolean) = runtimeSelected
