package org.btelman.controlsdk.interfaces

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.models.ComponentHolder

interface IControlSDKElement {
    fun onInitialize(context: Context, bundle: Bundle?){}

    companion object{
        fun instantiate(context: Context, componentHolder: ComponentHolder<*>) : IControlSDKElement?{
            return runCatching {
                return@runCatching (componentHolder.clazz.newInstance() as IControlSDKElement).also {
                    it.onInitialize(context, componentHolder.data)
                }
            }.getOrNull()
        }
    }
}