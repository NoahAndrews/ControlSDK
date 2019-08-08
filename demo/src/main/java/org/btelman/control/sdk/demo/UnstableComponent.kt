package org.btelman.control.sdk.demo

import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component

/**
 * Example of the ControlSDK handling unstable components
 */
class UnstableComponent : Component() {
    override fun enableInternal() {
        throw Exception("enableInternal Throwing Exception!!!")
    }

    override fun disableInternal() {
        throw Exception("disableInternal Throwing Exception!!!")
    }

    override fun getType(): ComponentType {
        return ComponentType.CUSTOM
    }
}