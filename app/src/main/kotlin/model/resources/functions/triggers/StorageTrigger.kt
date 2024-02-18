/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.resources.functions.triggers

import androidx.compose.runtime.Composable
import model.projects.CloudProject
import model.resources.buckets.BucketData
import view.menus.creation.triggers.storageTriggerContent

class StorageTrigger : Trigger {

    enum class EventType(val eventName: String) { CREATE("Create"), DELETE("Delete"), UPDATE("Update") }

    override val name = "Storage"
    override val shortName = "storage"
    override val postDeploymentMsg = "access storage buckets"
    override val triggerContent = @Composable { project: CloudProject, isReady: (nextEnabler: Boolean) -> Unit ->
        storageTriggerContent(this, project.buckets, isReady)
    }
    var bucketData: BucketData? = null
    var eventType = EventType.CREATE
}
