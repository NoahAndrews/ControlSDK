package org.btelman.controlsdk.interfaces

import android.content.Context
import android.os.Bundle
import android.os.Message
import kotlinx.coroutines.Deferred
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.ComponentHolder

/**
 * Base methods that any controller requires
 */
interface IController : IControlSDKElement{

    fun onInitializeController(context: Context, bundle: Bundle?){}

    /**
     * Enables the component asynchronously, and will return the result to a listening co-routine
     */
    fun enable() : Deferred<Boolean>

    /**
     * Disables the component asynchronously, and will return the result to a listening co-routine.
     * Treat this like the component is being destroyed
     */
    fun disable() : Deferred<Boolean>

    /**
     * Dispatches a message to the component's handler.
     */
    fun dispatchMessage(message: Message)

    /**
     * Set an event listener for this component
     */
    fun setEventListener(listener : ComponentEventListener?)

    /**
     * Gets the current type of the component based on Component.Companion.Event
     */
    fun getType() : ComponentType

    companion object{
        fun instantiate(context: Context, componentHolder: ComponentHolder<*>) : IController?{
            return runCatching {
                return@runCatching (componentHolder.clazz.newInstance() as IController).also {
                    it.onInitializeController(context, componentHolder.data)
                }
            }.getOrNull()
        }
    }
}