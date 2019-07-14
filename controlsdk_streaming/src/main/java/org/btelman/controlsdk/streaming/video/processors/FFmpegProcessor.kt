package org.btelman.controlsdk.streaming.video.processors

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.widget.Toast
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import org.btelman.controlsdk.enums.ComponentStatus
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.utils.VideoOutputStreamUtil
import java.io.PrintStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Process frames via FFmpeg
 */
class FFmpegProcessor : BaseVideoProcessor(), FFmpegExecuteResponseHandler {
    private var status: ComponentStatus = ComponentStatus.DISABLED
    private val streaming = AtomicBoolean(false)
    private val ffmpegRunning = AtomicBoolean(false)
    private var successCounter = 0
    private var streamInfo: StreamInfo? = null
    protected var ffmpeg : FFmpeg? = null
    var process : Process? = null

    override fun enable(context: Context, streamInfo: StreamInfo) {
        super.enable(context, streamInfo)
        ffmpeg = FFmpeg.getInstance(context.applicationContext)
        val latch = CountDownLatch(1)
        try {
            ffmpeg?.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onFinish() {
                    super.onFinish()
                    Log.d("FFMPEG", "onFinish")
                    latch.countDown()
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
            //TODO throw error?
            return
        }
        latch.await()
        this.streamInfo = streamInfo
        streaming.set(true)
    }

    override fun disable() {
        super.disable()
        streamInfo = null
        ffmpeg = null
        streaming.set(false)
        killFFmpeg()
    }

    /**
     * Kill FFmpeg by sending garbage data to the outputStream since it does not close on its own
     * correctly when closing the outputStream
     */
    private fun killFFmpeg() {
        val printStream = PrintStream(process?.outputStream)
        printStream.print("die") //does not matter what is here. Any garbage data should do the trick
        printStream.flush()
        printStream.close()
    }

    override fun processData(packet: ImageDataPacket) {
        if(streaming.get() /*&& limiter.tryAcquire() TODO?*/) {
            if (!ffmpegRunning.getAndSet(true)) {
                tryBootFFmpeg(packet.r)
            }
            try {
                VideoOutputStreamUtil.sendImageDataToProcess(process, packet)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Boot ffmpeg using config. If given a Rect, use that for resolution instead.
     */
    fun tryBootFFmpeg(r : Rect? = null){
        if(!streaming.get()){
            ffmpegRunning.set(false)
            status = ComponentStatus.DISABLED
            process?.outputStream?.close()
            return
        }
        try{
            bootFFmpeg(r)
        } catch (e: FFmpegCommandAlreadyRunningException) {
            status = ComponentStatus.ERROR
            e.printStackTrace()
            // Handle if FFmpeg is already running
        }
    }

    @Throws(FFmpegCommandAlreadyRunningException::class)
    private fun bootFFmpeg(r : Rect? = null) {
        val props = streamInfo ?: return
        successCounter = 0
        status = ComponentStatus.CONNECTING
        /*var xres = props.width
        var yres = props.height
        r?.let {
            xres = r.width()
            yres = r.height()
        }*/

        val rotationOption = props.orientation.ordinal //leave blank
        val builder = StringBuilder()
        for (i in 0..rotationOption){
            if(i == 0) builder.append("-vf transpose=1")
            else builder.append(",transpose=1")
        }
        val bitrate = props.bitrate
        val command = "-f image2pipe -codec:v mjpeg -i - -f mpegts -framerate ${props.framerate} -codec:v mpeg1video -b ${bitrate}k -minrate ${bitrate}k -maxrate ${bitrate}k -bufsize ${bitrate/1.5}k -bf 0 -tune zerolatency -preset ultrafast -pix_fmt yuv420p $builder ${props.endpoint}"
        ffmpeg?.execute(UUID, null, command.split(" ").toTypedArray(), this)
    }

    override fun onStart() {
        ffmpegRunning.set(true)
        @Suppress("ConstantConditionIf")
        if(shouldLog)
            Log.d(LOGTAG, "onStart")
    }

    override fun onProgress(message: String?) {
        @Suppress("ConstantConditionIf")
        if(shouldLog)
            Log.d(LOGTAG, "onProgress : $message")
        successCounter++
        status = when {
            successCounter > 5 -> ComponentStatus.STABLE
            successCounter > 2 -> ComponentStatus.INTERMITTENT
            else -> ComponentStatus.CONNECTING
        }
    }

    override fun onFailure(message: String?) {
        Log.e(LOGTAG, "progress : $message")
        status = ComponentStatus.ERROR
    }

    override fun onSuccess(message: String?) {
        @Suppress("ConstantConditionIf")
        if(shouldLog)
            Log.d(LOGTAG, "onSuccess : $message")
    }

    override fun onFinish() {
        @Suppress("ConstantConditionIf")
        if(shouldLog)
            Log.d(LOGTAG, "onFinish")
        ffmpegRunning.set(false)
        process?.destroy()
        process = null
        status = ComponentStatus.DISABLED
    }

    override fun onProcess(p0: Process?) {
        @Suppress("ConstantConditionIf")
        if(shouldLog)
            Log.d(LOGTAG, "onProcess")
        this.process = p0
    }

    companion object{
        const val shouldLog = true
        const val LOGTAG = "FFmpeg"
        val UUID = java.util.UUID.randomUUID().toString()
    }
}