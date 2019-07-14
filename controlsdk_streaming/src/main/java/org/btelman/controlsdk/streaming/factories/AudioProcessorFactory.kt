package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.audio.processors.BaseAudioProcessor
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor

/**
 * Handles creating the BaseVideoProcessor instance or putting the class in the main bundle
 */
object AudioProcessorFactory {
    fun findProcessor(bundle: Bundle): BaseAudioProcessor? {
        BaseFactory.checkForAndInitClass(getClassFromBundle(bundle), BaseAudioProcessor::class.java)?.let {
            return it
        }
        return DEFAULT.newInstance()
    }

    fun <T : BaseVideoProcessor> putClassInBundle(clazz: Class<T>, bundle: Bundle){
        BaseFactory.putClassInBundle(bundle, BUNDLE_ID, clazz)
    }

    fun getClassFromBundle(bundle: Bundle) : Class<*>?{
        return BaseFactory.getClassFromBundle(bundle, BUNDLE_ID)
    }

    val DEFAULT = BaseAudioProcessor::class.java
    const val BUNDLE_ID = "audioProcessorClass"
}
