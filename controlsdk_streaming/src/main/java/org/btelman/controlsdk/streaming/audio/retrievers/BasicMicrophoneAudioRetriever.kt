package org.btelman.controlsdk.streaming.audio.retrievers

import android.content.Context
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.utils.AudioRecordingThread
import org.btelman.controlsdk.streaming.utils.AudioUtil

/**
 * Grab audio from the microphone and send it to the processor when asked to
 */
class BasicMicrophoneAudioRetriever : BaseAudioRetriever(), AudioRecordingThread.AudioDataReceivedListener {

    private val recordingThread = AudioRecordingThread(this)

    private var dataArray : ByteArray? = null

    override fun enable(context: Context, streamInfo: StreamInfo) {
        super.enable(context, streamInfo)
        recordingThread.startRecording()
    }

    override fun disable() {
        super.disable()
        recordingThread.stopRecording()
    }

    override fun onAudioDataReceived(data: ShortArray?) {
        dataArray = data?.let { audioArr ->
            AudioUtil.ShortToByte_ByteBuffer_Method(audioArr)
        } //?: null
    }

    override fun retrieveAudioByteArray(): ByteArray? {
        return dataArray
    }
}