package org.btelman.controlsdk.tts

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.ComponentEventObject
import java.util.*

/**
 * Uses the System TTS system. This uses whatever the default engine is set to
 */
class SystemDefaultTTSComponent : TTSBaseComponent() {
    private var ttobj: TextToSpeech? = null
    private var context : Context? = null

    override fun onInitializeComponent(applicationContext: Context?, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        context = applicationContext
    }

    override fun enableInternal() {
        ttobj = TextToSpeech(context, TextToSpeech.OnInitListener {})
    }

    override fun disableInternal() {
        ttobj?.shutdown()
    }

    fun speakText(tts : TTSObject){
        ttobj?.setPitch(tts.pitch)
        @Suppress("DEPRECATION")
        val queueMode = getQueueMode(tts.shouldFlush)
        ttobj?.speak(tts.text, queueMode, null)
    }

    override fun handleExternalMessage(message: ComponentEventObject): Boolean {
        if(shouldHandle(message)){
            when(message.what){
                EVENT_MAIN -> process(message.data as TTSObject)
            }
        }
        return false //we want other handlers to be able to intercept this as well
    }

    private fun process(data: TTSObject) {
        if(data.isSpeakable) speakText(data)
        else{
            processCommand(data)
        }
    }

    private fun processCommand(data: TTSObject) {
        if(data.text.contains(".locale")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    ttobj?.language = Locale.forLanguageTag(data.text.split(" ")[1])
                } catch (e: Exception) {
                }
            }
        }
    }

    private fun shouldHandle(message: ComponentEventObject): Boolean {
        return (message.type == ComponentType.TTS && message.source != this)
    }

    private fun getQueueMode(shouldFlush: Boolean): Int {
        return if(shouldFlush){
            TextToSpeech.QUEUE_FLUSH
        }
        else{
            TextToSpeech.QUEUE_ADD
        }
    }

    override fun getType(): ComponentType {
        return ComponentType.TTS
    }
}