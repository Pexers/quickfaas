/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.triggers

import androidx.compose.runtime.Composable
import model.projects.CloudProject
import view.menus.creation.triggers.httpTriggerContent

class HttpTrigger : Trigger {
    override val name = "HTTP"
    override val shortName = "http"
    override val postDeploymentMsg = "trigger URL"
    override val triggerContent = @Composable { _: CloudProject, isReady: (nextEnabler: Boolean) -> Unit ->
        httpTriggerContent(this, isReady)
    }
}
