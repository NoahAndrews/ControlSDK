package org.btelman.controlsdk.interfaces

import android.os.Messenger
import org.btelman.controlsdk.models.ComponentHolder

/**
 * Interface for communicating with the robot service
 */
interface ControlSDKMessenger {

    /**
     * Enable the connection
     */
    fun enable()

    /**
     * Disable the connection
     */
    fun disable()

    /**
     * Reset the service, and pull new info. Generally called after settings were changed
     */
    fun reset()

    /**
     * Attach a custom component to the lifecycle. Must call reset() for changes to take effect
     */
    fun attachToLifecycle(component: ComponentHolder<*>)

    /**
     * detach a custom component from the lifecycle. Must call reset() for changes to take effect
     */
    fun detachFromLifecycle(component: ComponentHolder<*>)

    fun addListenerOrController(component: ComponentHolder<*>)

    fun removeListenerOrController(component: ComponentHolder<*>)

    fun onMessenger(messenger: Messenger?)
}