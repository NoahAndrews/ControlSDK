package org.btelman.controlsdk.streaming.utils

import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Utility functions for FFmpeg
 */
object FFmpegUtil {

    @Volatile
    private var ffmpegInitializedResult: Deferred<Boolean>? = null

    private val ffmpegInitStarted = AtomicBoolean(false)

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
    fun initFFmpegBlocking(ffmpeg: FFmpeg?) : Boolean{
        var result = false
        runBlocking {
            if(!ffmpegInitStarted.getAndSet(true)){
                ffmpegInitializedResult?: run{
                    ffmpegInitializedResult = initFFmpeg(ffmpeg)
                }
            }
            result = ffmpegInitializedResult?.await()!!
        }
        if(!result){
            throw FFmpegNotSupportedException("FFmpeg not supported on this device")
        }
        return result
    }

    suspend fun initFFmpeg(ffmpeg: FFmpeg?) = GlobalScope.async{
        val result = suspendCoroutine<Boolean> { cont ->
            try {
                ffmpeg?.loadBinary(object : LoadBinaryResponseHandler() {
                    override fun onFinish() {
                        super.onFinish()
                        cont.resume(true)
                    }
                })
            } catch (e: FFmpegNotSupportedException) {
                cont.resumeWith(Result.success(false))
            }
        }
        result
    }

    fun execute(ffmpeg: FFmpeg?, uuid: String, command: String, responseHandler: FFmpegExecuteResponseHandler) {
        ffmpeg?.execute(
            uuid, null, command.split(" ")
                .toTypedArray(), responseHandler)
    }
}