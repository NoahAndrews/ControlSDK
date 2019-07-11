package org.btelman.controlsdk.streaming.models

import android.media.MediaRecorder

/**
 * Store basic device info for camera and microphone
 *
 * @param camera the video source.
 * @param audio the recording source.
 *   See {@link MediaRecorder.AudioSource}
 */
data class CameraDeviceInfo (var camera: String, var audio : Int? = MediaRecorder.AudioSource.DEFAULT){
    companion object{
        /**
         * Use the same DeviceInfo constructor by using /dev/camera#,
         * and the component will just use the number for the camera type
         * Does not use /dev/video# since those are reserved for the web cam component
         */
        fun fromCamera(id : Int = 0) : CameraDeviceInfo{
            return CameraDeviceInfo("/dev/camera$id")
        }
    }
}