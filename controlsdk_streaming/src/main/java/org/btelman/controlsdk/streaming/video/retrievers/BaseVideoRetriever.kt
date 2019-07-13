package org.btelman.controlsdk.streaming.video.retrievers

import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Base class for retrieving frames to send to the video processor
 */
abstract class BaseVideoRetriever {
    open fun enable(streamInfo: StreamInfo){

    }

    open fun disable(){

    }

    abstract fun grabImageData() : ImageDataPacket
}
