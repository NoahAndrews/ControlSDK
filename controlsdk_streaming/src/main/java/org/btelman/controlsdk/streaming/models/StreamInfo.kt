package org.btelman.controlsdk.streaming.models

import android.os.Bundle
import org.btelman.controlsdk.streaming.enums.Orientation

/**
 * Data class that will store info for the stream
 */
data class StreamInfo(val endpoint : String,
                      val width : Int = 640,
                      val height : Int = 480,
                      val bitrate : Int = 512,
                      val framerate : Int = 30,
                      val orientation : Orientation = Orientation.DIR_90,
                      val deviceInfo: CameraDeviceInfo = CameraDeviceInfo.fromCamera(0)){

    fun toBundle() : Bundle{
        return addToExistingBundle(Bundle())
    }

    fun addToExistingBundle(bundle : Bundle) : Bundle{
        bundle.apply {
            putString("endpoint", endpoint)
            putInt("width", width)
            putInt("height", height)
            putInt("framerate", framerate)
            putInt("orientation", orientation.value)
            putBundle("deviceInfo", deviceInfo.toBundle())
        }
        return bundle
    }

    companion object{
        @Throws(NullPointerException::class)
        fun fromBundle(bundle : Bundle) : StreamInfo{
            return StreamInfo(bundle.getString("endpoint")!!,
                bundle.getInt("width"),
                bundle.getInt("height"),
                bundle.getInt("bitrate"),
                bundle.getInt("framerate"),
                Orientation.forValue(bundle.getInt("orientation"))!!,
                CameraDeviceInfo.fromBundle(bundle.getBundle("deviceInfo")!!))
        }
    }
}