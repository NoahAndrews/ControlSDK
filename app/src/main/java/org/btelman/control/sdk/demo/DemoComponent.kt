package org.btelman.control.sdk.demo

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.models.ComponentEventObject
import org.btelman.controlsdk.tts.TTSBaseComponent

/**
 * Created by Brendon on 7/2/2019.
 */
class DemoComponent : Component() {

    override fun onInitializeComponent(applicationContext: Context?, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
    }

    override fun enableInternal() {
        eventDispatcher?.handleMessage(ComponentEventObject(ComponentType.TTS, EVENT_MAIN, TTSBaseComponent.TTSObject(TTSBaseComponent.TTS_OK
            , TTSBaseComponent.COMMAND_PITCH, shouldFlush = false), this))
        eventDispatcher?.handleMessage(ComponentEventObject(ComponentType.TTS, EVENT_MAIN, TTSBaseComponent.TTSObject(TTSBaseComponent.TTS_DISCONNECTED
            , TTSBaseComponent.COMMAND_PITCH, shouldFlush = false), this))
    }

    override fun disableInternal() {

    }

    override fun getType(): ComponentType {
        return ComponentType.CUSTOM
    }
}