package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.audio.retrievers.BaseAudioRetriever
import org.btelman.controlsdk.streaming.audio.retrievers.BasicMicrophoneAudioRetriever

/**
 * Handles creating the BaseVideoProcessor instance or putting the class in the main bundle
 */
object AudioRetrieverFactory {
    fun findRetriever(bundle: Bundle): BaseAudioRetriever? {
        BaseFactory.checkForAndInitClass(getClassFromBundle(bundle), BaseAudioRetriever::class.java)?.let {
            return it
        }
        return DEFAULT.newInstance()
    }

    fun <T : BaseAudioRetriever> putClassInBundle(clazz: Class<T>, bundle: Bundle){
        BaseFactory.putClassInBundle(bundle, BUNDLE_ID, clazz)
    }

    fun getClassFromBundle(bundle: Bundle) : Class<*>?{
        return BaseFactory.getClassFromBundle(bundle, BUNDLE_ID)
    }

    val DEFAULT = BasicMicrophoneAudioRetriever::class.java
    const val BUNDLE_ID = "audioRetrieverClass"
}
