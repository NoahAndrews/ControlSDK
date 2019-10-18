package org.btelman.controlsdk.streaming.utils

import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicInteger

/**
 * Utility functions for FFmpeg
 */
object FFmpegUtil {

    private val ffmpegResult = AtomicInteger(-1)

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

    // blocking ffmpeg initialize that will only initialize once,
    // and block until result on the other threads that called this same function instead of that
    // one initializing as well
    @Synchronized
    fun initFFmpegAsync(ffmpeg: FFmpeg?, onResult : (Boolean)->Unit){
        if(ffmpegResult.get() == -1){
            try {
                ffmpeg?.loadBinary(object : LoadBinaryResponseHandler() {
                    override fun onFinish() {
                        super.onFinish()
                        ffmpegResult.set(1)
                        onResult(true)
                    }
                })
            } catch (e: FFmpegNotSupportedException) {
                ffmpegResult.set(0)
                onResult(false)
            }
        }
        else{
            onResult(ffmpegResult.get() == 1)
        }
    }

    fun execute(ffmpeg: FFmpeg?, uuid: String, command: String, responseHandler: FFmpegExecuteResponseHandler) {
        ffmpeg?.execute(
            uuid, null, command.split(" ")
                .toTypedArray(), responseHandler)
    }
}