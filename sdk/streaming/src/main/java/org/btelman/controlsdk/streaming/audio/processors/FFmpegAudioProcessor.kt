package org.btelman.controlsdk.streaming.audio.processors

import android.content.Context
import android.util.Log
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import org.btelman.controlsdk.enums.ComponentStatus
import org.btelman.controlsdk.streaming.models.AudioPacket
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.utils.FFmpegUtil
import org.btelman.controlsdk.streaming.utils.OutputStreamUtil
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Process audio from a source and send it to a specified endpoint with FFmpeg
 */
open class FFmpegAudioProcessor : BaseAudioProcessor(), FFmpegExecuteResponseHandler {

    private var lastTimecode: Long = 0L
    private var endpoint: String? = null
    private var status: ComponentStatus = ComponentStatus.DISABLED
    private var ffmpegRunning = AtomicBoolean(false)

    private var ffmpeg : FFmpeg? = null

    private var process: Process? = null

    private var successCounter: Int = 0

    override fun enable(context: Context, streamInfo: StreamInfo) {
        super.enable(context, streamInfo)
        endpoint = streamInfo.audioEndpoint ?: return//?: else we can't do anything
        ffmpeg = FFmpeg.getInstance(context)
        if(!FFmpegUtil.initFFmpegBlocking(ffmpeg)) return //TODO throw error
    }

    override fun disable() {
        super.disable()
        process?.destroy()
        process = null
        ffmpegRunning.set(false)
        FFmpegUtil.killFFmpeg(process)
    }

    override fun onStart() {
        ffmpegRunning.set(true)
        Log.d(TAG, "onStart")
    }

    override fun processAudioByteArray(data: AudioPacket) {
        ensureFFmpegStarted()
        process?.let { _process ->
            if(data.timecode != lastTimecode){ //make sure we only send each packet once
                lastTimecode = data.timecode
                OutputStreamUtil.handleSendByteArray(_process.outputStream, data.b)
            }
        }
    }

    protected open fun ensureFFmpegStarted() {
        try {
            if(!ffmpegRunning.get()){
                successCounter = 0
                status = ComponentStatus.CONNECTING
                val bitrate = 32 //TODO make adjustable
                val volumeBoost = 1 //TODO make adjustable
                val separator = " "
                val command = "-f s16be -i - -f mpegts -codec:a mp2 -b:a ${bitrate}k -ar 44100" +
                        "$separator-muxdelay 0.001 -filter:a volume=$volumeBoost" +
                        "$separator$endpoint"
                FFmpegUtil.execute(ffmpeg, UUID, command, this)
            }
        } catch (e: Exception) {
            status = ComponentStatus.ERROR
            e.printStackTrace()
        }
    }

    override fun onProgress(message: String?) {
        successCounter++
        status = ComponentStatus.STABLE
    }

    override fun onFailure(message: String?) {
        status = ComponentStatus.ERROR //TODO
        Log.e(TAG, "progress : $message")
    }

    override fun onSuccess(message: String?) {
        Log.d(TAG, "onSuccess : $message")
    }

    override fun onFinish() {
        Log.d(TAG, "onFinish")
        status = ComponentStatus.DISABLED
        ffmpegRunning.set(false)
    }

    override fun onProcess(p0: Process?) {
        process = p0
        Log.d(TAG, "onProcess")
    }

    companion object {
        const val TAG = "Audio"
        val UUID = java.util.UUID.randomUUID().toString()
    }
}