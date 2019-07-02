package org.btelman.controlsdk.interfaces

import android.os.Bundle

/**
 * Hold data about a component so the service can instantiate it
 */
data class ComponentHolder<T : Component>(val clazz: Class<T>, val data : Bundle?)