package org.btelman.control.sdk.demo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.video.retrievers.BaseVideoRetriever


/**
 * Just an empty class to test. Draws a canvas with the color set to orange
 */
class DummyCanvasRetriever : BaseVideoRetriever() {
    override fun grabImageData(): ImageDataPacket? {
        //draw a color to a bitmap and send it to be processed
        val myBitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.RGB_565)
        val canvas = Canvas()
        canvas.setBitmap(myBitmap)
        canvas.drawColor(Color.parseColor("#e9a811"))
        return ImageDataPacket(myBitmap)
    }
}