package org.btelman.controlsdk.models

import org.btelman.controlsdk.enums.ComponentType
import java.util.*

/**
 * Object for storing event details
 */
class ComponentEventObject(
        val type : ComponentType,
        val what : Int,
        val data : Any?,
        source: Any?) : EventObject(source)