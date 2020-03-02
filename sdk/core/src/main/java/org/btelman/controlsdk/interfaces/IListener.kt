package org.btelman.controlsdk.interfaces

import android.content.Context
import android.os.Bundle
import android.os.Message
import org.btelman.controlsdk.enums.ComponentStatus
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.enums.ServiceStatus
import org.btelman.controlsdk.models.ComponentHolder

/**
 * Base methods that any listener requires
 */
interface IListener : IControlSDKElement{
    fun onInitializeListener(context: Context, bundle: Bundle?){}

    //TODO
    fun onError(origin : Class<*>?, exception: Exception){}

    fun onServiceStateChange(status : ServiceStatus){}
    fun onComponentStatus(clazz : Class<*>, componentStatus: ComponentStatus){}
    fun onComponentAdded(component: ComponentHolder<*>){}
    fun onComponentRemoved(component: ComponentHolder<*>){}

    /**
     * Dispatches a message to the component's handler.
     */
    fun dispatchMessage(message: Message){}

    /**
     * Set an event listener for this component
     */
    fun setEventListener(listener : ComponentEventListener?){

    }

    /**
     * Gets the current type of the component based on Component.Companion.Event
     */
    fun getComponentTypesForListening() : List<ComponentType>?{
        return null
    }

    companion object{
        fun instantiate(context: Context, componentHolder: ComponentHolder<*>) : IListener?{
            return runCatching {
                return@runCatching (componentHolder.clazz.newInstance() as IListener).also {
                    it.onInitializeListener(context, componentHolder.data)
                }
            }.getOrNull()
        }
    }
}