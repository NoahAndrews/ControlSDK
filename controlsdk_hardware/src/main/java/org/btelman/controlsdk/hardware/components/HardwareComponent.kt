package org.btelman.controlsdk.hardware.components

import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.hardware.translators.HardwareTranslator
import org.btelman.controlsdk.models.Component

/**
 * Component that receives messages and handles sending them to actual hardware
 */
class HardwareComponent : Component() {
    val translatorComponent : HardwareTranslator? = null
    val communicationDriverComponent : CommunicationDriverComponent? = null

    override fun enableInternal() {

    }

    override fun disableInternal() {

    }

    override fun getType(): ComponentType {
        return ComponentType.HARDWARE
    }
}