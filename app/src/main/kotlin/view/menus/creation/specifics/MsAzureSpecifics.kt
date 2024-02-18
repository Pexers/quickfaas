/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package view.menus.creation.specifics

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.specifics.MsAzureSpecifics
import view.common.dropDownField

@Composable
fun msAzurePreRequisites(cs: MsAzureSpecifics, scope: CoroutineScope, onFinish: () -> Unit) {
    var subscriptions by remember { mutableStateOf(cs.subscriptions.map { sub -> sub.displayName }) }
    var subEnabler by remember { mutableStateOf(cs.subscription.subscriptionId.isEmpty()) }

    if (!subEnabler) onFinish()

    if (subscriptions.isEmpty()) scope.launch {
        subscriptions = cs.requestSubscriptions().map { sub -> sub.displayName }
    }

    dropDownField(title = "Subscription",
        defaultOption = cs.subscription.displayName,
        options = subscriptions,
        hint = "Select a subscription",
        enabled = subEnabler,
        onChange = { idx, _ ->
            cs.setSubscriptionData(idx)
            subEnabler = false
        })
}
