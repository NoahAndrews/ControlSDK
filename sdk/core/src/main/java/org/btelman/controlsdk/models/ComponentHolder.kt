package org.btelman.controlsdk.models

import android.os.Bundle
import org.btelman.controlsdk.interfaces.IControlSDKElement

/**
 * Hold data about a component so the service can instantiate it
 */
data class ComponentHolder<T : IControlSDKElement>(val clazz: Class<T>, val data : Bundle?)