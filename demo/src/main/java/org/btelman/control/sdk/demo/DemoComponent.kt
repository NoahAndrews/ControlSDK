package org.btelman.control.sdk.demo

import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.models.ComponentEventObject
import org.btelman.controlsdk.tts.TTSBaseComponent

/**
 * Demo
 */
class DemoComponent : Component() {

    //override fun onInitializeComponent available

    override fun enableInternal() {
        handler.post {
            eventDispatcher?.handleMessage(ComponentEventObject(ComponentType.TTS, EVENT_MAIN,
                TTSBaseComponent.TTSObject(TTSBaseComponent.TTS_OK), this))
            eventDispatcher?.handleMessage(ComponentEventObject(ComponentType.TTS, EVENT_MAIN,
                TTSBaseComponent.TTSObject(TTSBaseComponent.TTS_DISCONNECTED
                    , TTSBaseComponent.COMMAND_PITCH, shouldFlush = false), this))
            eventDispatcher?.handleMessage(ComponentEventObject(ComponentType.TTS, EVENT_MAIN,
                TTSBaseComponent.TTSObject("Lorem Ipsum"), this))
            eventDispatcher?.handleMessage(ComponentEventObject(ComponentType.TTS, EVENT_MAIN,
                TTSBaseComponent.TTSObject("Longer message that says more things"), this))
        }
    }

    override fun disableInternal() {

    }

    override fun getType(): ComponentType {
        return ComponentType.CUSTOM
    }
}