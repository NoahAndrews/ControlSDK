package org.btelman.controlsdk.interfaces

import org.btelman.controlsdk.enums.ComponentType
import java.util.*

interface ComponentEventListener : EventListener{
    fun handleMessage(eventObject: ComponentEventObject)
    /**
     * Send an event to whatever is listening.
     *
     * if source not the same class as type, assume that we want to send a message to type instead
     * of a broadcast to all components
     */
    fun handleMessage(type: ComponentType, what: Int, data: Any?, source : Any?) {
        handleMessage(ComponentEventObject(type, what, data, source))
    }
}
