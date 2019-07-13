package org.btelman.controlsdk.streaming.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.YuvImage
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import java.io.IOException
import java.io.OutputStream

/**
 * Utilities for video processors that transfer image data via a process or an OutputStream
 */
object VideoOutputStreamUtil {
    fun sendImageDataToProcess(process : Process?, packet: ImageDataPacket) : Boolean{
        return process?.let { _process ->
            try { //send the data and catch IO exception if it fails
                sendImageToOutputStream(_process.outputStream, packet)
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        } ?: false
    }

    fun sendImageToOutputStream(outputStream: OutputStream, packet: ImageDataPacket) : Boolean{
        return (packet.b as? ByteArray)?.let { _ ->
            processByteArray(outputStream, packet)
        } ?: processBitmap(outputStream, packet)
    }

    private fun processBitmap(outputStream: OutputStream, packet: ImageDataPacket): Boolean {
        return (packet.b as? Bitmap)?.compress(
            Bitmap.CompressFormat.JPEG,
            100, outputStream) ?: false //TODO change quality?
    }

    private fun processByteArray(outputStream: OutputStream, it: ImageDataPacket) : Boolean{
        when (it.format) {
            ImageFormat.JPEG -> {
                outputStream.write(it.b as ByteArray)
            }
            ImageFormat.NV21 -> {
                it.r?.let { rect ->
                    val im = YuvImage(it.b as ByteArray, it.format, rect.width(), rect.height(), null)
                    return im.compressToJpeg(rect, 100, outputStream) //TODO change quality?
                }
            }
            else -> {
            }
        }
        return true
    }
}