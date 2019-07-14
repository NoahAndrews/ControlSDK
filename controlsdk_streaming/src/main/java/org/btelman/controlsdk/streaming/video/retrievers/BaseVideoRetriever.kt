package org.btelman.controlsdk.streaming.video.retrievers

import android.content.Context
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Base class for retrieving frames to send to the video processor
 */
abstract class BaseVideoRetriever {
    protected var context: Context? = null

    open fun enable(context : Context, streamInfo: StreamInfo){
        this.context = context
    }

    open fun disable(){

    }

    abstract fun grabImageData() : ImageDataPacket?
}
