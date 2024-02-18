/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.authentication

object TokenListeners {
    private val listeners = mutableMapOf<Int, (cpShortName: String) -> Unit>()

    fun addTokenListener(windowId: Int, callback: (cpShortName: String) -> Unit) {
        listeners[windowId] = callback
    }

    fun notifyTokenListeners(cpShortName: String) = listeners.forEach { listener -> listener.value(cpShortName) }

}
