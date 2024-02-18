/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.triggers

import androidx.compose.runtime.Composable
import model.projects.CloudProject

interface Trigger {
    val name: String
    val shortName: String
    val postDeploymentMsg: String
    val triggerContent: @Composable (project: CloudProject, isReady: (nextEnabler: Boolean) -> Unit) -> Unit
}
