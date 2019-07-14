package org.btelman.controlsdk.streaming.components

import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component

/**
 * Audio component to handle doing stuff with audio
 */
open class AudioComponent : Component() {

    override fun enableInternal() {

    }

    override fun disableInternal() {

    }

    override fun getType(): ComponentType {
        return ComponentType.STREAMING
    }

}