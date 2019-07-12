package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor

/**
 * Handles creating the BaseVideoProcessor instance or putting the class in the main bundle
 */
object VideoProcessorFactory {
    fun findProcessor(bundle: Bundle): BaseVideoProcessor? {
        bundle.getSerializable("videoProcessorClass")?.let {
            (it as? Class<*>)?.let {clazz ->
                if(clazz.isAssignableFrom(BaseVideoProcessor::class.java)){
                    return clazz.newInstance() as BaseVideoProcessor
                }
            }
        }
        return DEFAULT.newInstance()
    }

    fun <T : BaseVideoProcessor> putClassInBundle(clazz: Class<T>, bundle: Bundle){
        bundle.putSerializable("videoProcessorClass", clazz)
    }

    //TODO replace with non-abstract class when we get FFMpeg in here
    val DEFAULT = BaseVideoProcessor::class.java
}
