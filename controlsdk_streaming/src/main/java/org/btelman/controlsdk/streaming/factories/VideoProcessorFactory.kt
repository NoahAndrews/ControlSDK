package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor
import org.btelman.controlsdk.streaming.video.processors.FFmpegVideoProcessor

/**
 * Handles creating the BaseVideoProcessor instance or putting the class in the main bundle
 */
object VideoProcessorFactory {
    fun findProcessor(bundle: Bundle): BaseVideoProcessor? {
        BaseFactory.checkForAndInitClass(getClassFromBundle(bundle), BaseVideoProcessor::class.java)?.let {
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

    val DEFAULT = FFmpegVideoProcessor::class.java
    const val BUNDLE_ID = "videoProcessorClass"
}
