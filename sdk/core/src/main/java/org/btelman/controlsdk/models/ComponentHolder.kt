package org.btelman.controlsdk.models

import android.os.Bundle
import org.btelman.controlsdk.interfaces.IControlSDKElement
import java.util.*

/**
 * Hold data about a component so the service can instantiate it
 */
data class ComponentHolder<T : IControlSDKElement>(
    /**
     * Class of the Component that will be instantiated
     */
    val clazz: Class<T>,
    /**
     * Bundle that will hold the data for the class
     */
    val data : Bundle?,
    /**
     * Unique tag for the component. If the service encounters a tag that already is active,
     * it will destroy the old tag
     */
    val uniqueTag : String = UUID.randomUUID().toString()
)