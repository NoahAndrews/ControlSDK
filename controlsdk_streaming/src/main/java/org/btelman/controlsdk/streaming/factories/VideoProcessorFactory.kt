package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor
import org.btelman.controlsdk.streaming.video.processors.FFmpegProcessor

/**
 * Handles creating the BaseVideoProcessor instance or putting the class in the main bundle
 */
object VideoProcessorFactory {
    fun findProcessor(bundle: Bundle): BaseVideoProcessor? {
        getClassFromBundle(bundle)?.let {
            (it as? Class<*>)?.let {clazz ->
                if(BaseVideoProcessor::class.java.isAssignableFrom(clazz)){
                    return clazz.newInstance() as BaseVideoProcessor
                }
            }
        }
        return DEFAULT.newInstance()
    }

    fun <T : BaseVideoProcessor> putClassInBundle(clazz: Class<T>, bundle: Bundle){
        bundle.putSerializable("videoProcessorClass", clazz)
    }

    fun getClassFromBundle(bundle: Bundle) : Class<*>?{
        return bundle.getSerializable("videoProcessorClass") as? Class<*>
    }

    val DEFAULT = FFmpegProcessor::class.java
}
