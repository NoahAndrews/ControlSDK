package org.btelman.controlsdk.streaming.video.processors

import android.content.Context
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Handles core logic of processing the video and usually sending it out to a service
 */
abstract class BaseVideoProcessor{

    open fun enable(context: Context, streamInfo: StreamInfo){

    }

    open fun disable(){

    }

    abstract fun processData(packet: ImageDataPacket)
}
