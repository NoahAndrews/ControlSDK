package org.btelman.controlsdk.hardware.protocols

import android.util.Log
import org.btelman.controlsdk.hardware.components.ControlTranslatorComponent
import java.nio.charset.Charset

/**
 * Arduino String sending converter
 */
class ArduinoSendBytesProtocol : ControlTranslatorComponent() {

    override fun onStringCommand(command: String) {
        super.onStringCommand(command)
        sendBytesWithTerminator(command)
    }

    override fun onStop(any: Any?) {
        super.onStop(any)
        sendBytesWithTerminator("stop")
    }

    private fun sendBytesWithTerminator(string : String){
        val messageWithTerminator = "$string\r\n"
        Log.d(TAG, "message = $messageWithTerminator")
        sendToDevice(messageWithTerminator.toLowerCase().toByteArray(Charset.forName("UTF-8")))
    }

    companion object {
        const val TAG = "ArduinoProtocol"
    }
}