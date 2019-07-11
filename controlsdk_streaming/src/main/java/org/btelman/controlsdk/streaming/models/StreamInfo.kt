package org.btelman.controlsdk.streaming.models

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
                      val deviceInfo: CameraDeviceInfo = CameraDeviceInfo.fromCamera(0))