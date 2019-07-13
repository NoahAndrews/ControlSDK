package org.btelman.controlsdk.streaming.video.processors

import android.graphics.Bitmap
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Handles core logic of processing the video and usually sending it out to a service
 */
abstract class BaseVideoProcessor{

    open fun enable(streamInfo: StreamInfo){

    }

    open fun disable(){

    }

    abstract fun processFrame(data : Array<Byte>) : Boolean
    abstract fun processFrame(bitmap : Bitmap) : Boolean
}
