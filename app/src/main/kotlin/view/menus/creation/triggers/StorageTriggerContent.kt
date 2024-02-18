/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.triggers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import model.resources.buckets.BucketData
import model.resources.functions.triggers.StorageTrigger
import model.resources.functions.triggers.StorageTrigger.EventType
import view.common.dropDownField
import view.styles.Modifiers

@Composable
fun storageTriggerContent(trigger: StorageTrigger, buckets: List<BucketData>, isReady: (nextEnabler: Boolean) -> Unit) {
    var bucketSelected by remember { mutableStateOf(trigger.bucketData?.name?.isNotEmpty() ?: false) }

    isReady(nextEnabler(bucketSelected))

    Column(
        Modifiers.PageModifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        dropDownField(title = "Bucket",
            defaultOption = trigger.bucketData?.name ?: "",
            options = buckets.map { bucket -> bucket.name },
            hint = "Select a bucket",
            onChange = { idx, _ ->
                trigger.bucketData = buckets[idx]
                bucketSelected = true
            })
        dropDownField(title = "Event type",
            defaultOption = trigger.eventType.eventName,
            options = EventType.values().map { event -> event.eventName },
            hint = "Select the event type",
            onChange = { _, value ->
                trigger.eventType = EventType.valueOf(value.uppercase())
            })
    }
}

private fun nextEnabler(bucketSelected: Boolean) = bucketSelected

