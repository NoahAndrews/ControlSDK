package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.video.retrievers.BaseVideoRetriever
import org.btelman.controlsdk.streaming.video.retrievers.api16.Camera1SurfaceTextureComponent
import org.btelman.controlsdk.streaming.video.retrievers.api21.Camera2SurfaceTextureComponent

object VideoRetrieverFactory {
    fun findRetriever(bundle: Bundle): BaseVideoRetriever? {
        getClassFromBundle(bundle)?.let {
            (it as? Class<*>)?.let {clazz ->
                if(clazz.isAssignableFrom(BaseVideoRetriever::class.java)){
                    return clazz.newInstance() as BaseVideoRetriever
                }
            }
        }
        StreamInfo.fromBundle(bundle)?.also {streamInfo ->
            when {
                streamInfo.deviceInfo.camera.contains("/dev/video") -> TODO("USB Camera retriever class")
                streamInfo.deviceInfo.camera.contains("/dev/camera") -> return if(Camera2SurfaceTextureComponent.isSupported()){
                    Camera2SurfaceTextureComponent()
                } else{
                    Camera1SurfaceTextureComponent()
                }
                streamInfo.deviceInfo.camera.contains("http") -> TODO("Camera stream from other device")
            }
        }
        return DEFAULT.newInstance()
    }

    fun <T : BaseVideoRetriever> putClassInBundle(clazz: Class<T>, bundle: Bundle){
        bundle.putSerializable("videoRetrieverClass", clazz)
    }

    fun getClassFromBundle(bundle: Bundle) : Class<*>?{
        return bundle.getSerializable("videoRetrieverClass") as? Class<*>
    }

    val DEFAULT = Camera1SurfaceTextureComponent::class.java
}
