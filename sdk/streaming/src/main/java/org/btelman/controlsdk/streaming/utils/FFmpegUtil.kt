package org.btelman.controlsdk.streaming.utils

import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import java.io.PrintStream
import java.util.concurrent.CountDownLatch

/**
 * Utility functions for FFmpeg
 */
object FFmpegUtil {

    /**
     * Kill FFmpeg by sending garbage data to the outputStream since it does not close on its own
     * correctly when closing the outputStream
     */
    fun killFFmpeg(process: Process?) {
        process?.let {
            val printStream = PrintStream(it.outputStream)
            printStream.print("die") //does not matter what is here. Any garbage data should do the trick
            printStream.flush()
            printStream.close()
        }
    }

    fun initFFmpegBlocking(ffmpeg: FFmpeg?) : Boolean{
        val latch = CountDownLatch(1)
        try {
            ffmpeg?.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onFinish() {
                    super.onFinish()
                    latch.countDown()
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
            return false
        }
        latch.await()
        return true
    }

    fun execute(ffmpeg: FFmpeg?, uuid: String, command: String, responseHandler: FFmpegExecuteResponseHandler) {
        ffmpeg?.execute(
            uuid, null, command.split(" ")
                .toTypedArray(), responseHandler)
    }
}