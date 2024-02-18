/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.configuration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import model.CloudProvider
import view.common.dropDownField
import view.common.textInputField
import view.menus.creation.triggers.triggerPage
import view.navigation.PageNavigation
import view.navigation.navigationButtons
import view.styles.Modifiers.PageModifier

@Composable
fun configurationPage(cp: CloudProvider, pageNav: PageNavigation) {
    val projFunc = cp.project.function
    var specificsFinished by remember { mutableStateOf(cp.cloudSpecifics == null) }
    var projects by remember { mutableStateOf(cp.projects.map { proj -> proj.name }) }
    var buckets by remember { mutableStateOf(cp.project.buckets.map { bucket -> bucket.name }) }

    // Next enablers
    var functionName by remember { mutableStateOf(projFunc.name) }
    var projectSelected by remember { mutableStateOf(cp.project.projectData.name.isNotEmpty()) }
    var locationSelected by remember { mutableStateOf(projFunc.location.isNotEmpty()) }
    var bucketSelected by remember { mutableStateOf(projFunc.bucket.bucketData.name.isNotEmpty()) }

    val scope = rememberCoroutineScope()

    if (specificsFinished && cp.projects.isEmpty()) {
        scope.launch { projects = cp.requestProjects().map { proj -> proj.name } }
    }

    Column(
        PageModifier, verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Some cloud providers may need specific prerequisites
        cp.cloudSpecifics?.preRequisites?.invoke(scope) { specificsFinished = true }

        dropDownField(title = "Project",
            defaultOption = cp.project.projectData.name,
            options = projects,
            hint = "Select a project",
            enabled = specificsFinished,
            onChange = { idx, _ ->
                cp.setProjectData(idx)
                buckets = listOf()
                bucketSelected = false
                projectSelected = true
                scope.launch { buckets = cp.project.requestBuckets().map { buck -> buck.name } }
            })

        textInputField(title = "Function name",
            fieldValue = functionName,
            hint = "Function name",
            enabled = specificsFinished,
            onChange = {
                projFunc.name = it
                functionName = it
            })

        dropDownField(title = "Location",
            defaultOption = projFunc.location,
            options = projFunc.locations,
            hint = "Select a location",
            enabled = specificsFinished,
            onChange = { _, value ->
                projFunc.location = value
                locationSelected = true
            })

        dropDownField(title = "Bucket",
            defaultOption = projFunc.bucket.bucketData.name,
            options = buckets,
            hint = "Select a bucket",
            onChange = { idx, _ ->
                projFunc.bucket.bucketData = cp.project.buckets[idx]
                bucketSelected = true
            })
    }

    navigationButtons(
        pageNav = pageNav,
        nextPage = { triggerPage(cp, pageNav) },
        nextEnabler = nextEnabler(projectSelected, functionName, locationSelected, bucketSelected)
    )
}

private fun nextEnabler(
    projectSelected: Boolean, funcName: String, locationSelected: Boolean, bucketSelected: Boolean
) = projectSelected && funcName.length >= 4 && locationSelected && bucketSelected
