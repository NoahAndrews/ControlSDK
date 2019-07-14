package org.btelman.controlsdk.streaming.factories

import android.os.Bundle
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.video.retrievers.BaseVideoRetriever
import org.btelman.controlsdk.streaming.video.retrievers.api16.Camera1SurfaceTextureComponent
import org.btelman.controlsdk.streaming.video.retrievers.api21.Camera2SurfaceTextureComponent

object VideoRetrieverFactory {
    fun findRetriever(bundle: Bundle): BaseVideoRetriever? {
        BaseFactory.checkForAndInitClass(getClassFromBundle(bundle), BaseVideoRetriever::class.java)?.let {
            return it
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
        BaseFactory.putClassInBundle(bundle, BUNDLE_ID, clazz)
    }

    fun getClassFromBundle(bundle: Bundle) : Class<*>?{
        return BaseFactory.getClassFromBundle(bundle, BUNDLE_ID)
    }

    val DEFAULT = Camera1SurfaceTextureComponent::class.java
    const val BUNDLE_ID = "videoRetrieverClass"
}
