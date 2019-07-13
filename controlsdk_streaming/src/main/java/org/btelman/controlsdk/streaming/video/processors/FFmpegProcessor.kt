package org.btelman.controlsdk.streaming.video.processors

import android.graphics.Bitmap
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Process frames via FFmpeg
 */
class FFmpegProcessor : BaseVideoProcessor() {
    override fun enable(streamInfo: StreamInfo) {
        super.enable(streamInfo)
    }

    override fun disable() {
        super.disable()
    }

    override fun processFrame(data: Array<Byte>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processFrame(bitmap: Bitmap): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}