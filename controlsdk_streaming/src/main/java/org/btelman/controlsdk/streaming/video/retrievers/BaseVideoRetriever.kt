package org.btelman.controlsdk.streaming.video.retrievers

import android.graphics.Bitmap
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Base class for retrieving frames to send to the video processor
 */
abstract class BaseVideoRetriever {
    open fun enable(streamInfo: StreamInfo){

    }

    open fun disable(){

    }

    abstract fun grabFrameByteArray() : Array<Byte>
    abstract fun grabBitmap() : Bitmap
}
