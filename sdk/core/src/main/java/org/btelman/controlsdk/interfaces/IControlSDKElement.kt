package org.btelman.controlsdk.interfaces

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.models.ComponentHolder

interface IControlSDKElement {
    /**
     * Any initialization logic can go here. Whether or not the element stores the application context is up to it
     */
    fun onInitializeComponent(applicationContext: Context, bundle: Bundle?)

    /**
     * Component has been removed from the service.
     * Any shutdown logic that was not already done should go here
     */
    fun onRemoved()

    companion object{
        fun instantiate(context: Context, componentHolder: ComponentHolder<*>) : IControlSDKElement?{
            return runCatching {
                return@runCatching (componentHolder.clazz.newInstance() as IControlSDKElement).also {
                    it.onInitializeComponent(context, componentHolder.data)
                }
            }.getOrNull()
        }
    }
}