package org.btelman.control.sdk.demo

import android.util.Log
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor

/**
 * Created by Brendon on 7/14/2019.
 */
class DummyVideoProcessor : BaseVideoProcessor() {
    override fun processData(packet: ImageDataPacket) {
        Log.d("DummyVideoProcessor", "processData")
        Thread.sleep(1000)
    }
}