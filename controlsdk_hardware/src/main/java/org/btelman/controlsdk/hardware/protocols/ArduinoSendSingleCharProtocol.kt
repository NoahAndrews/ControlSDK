package org.btelman.controlsdk.hardware.protocols

import android.content.Context
import android.util.Log
import org.btelman.controlsdk.hardware.ControlComponent
import java.nio.charset.Charset

/**
 * Sends the first character of desired command without sending /r/n
 *
 * Added in case somebody runs into a board that cannot properly handle parsing a multi character command
 */
class ArduinoSendSingleCharProtocol(context: Context) :
        ControlComponent(context) {

    override fun onStringCommand(command: String) {
        super.onStringCommand(command)
        sendByte(command)
    }

    override fun onStop(any: Any?) {
        super.onStop(any)
        sendByte("s")
    }

    private fun sendByte(string : String){
        Log.d(TAG, "message = ${string[0]}")
        sendToDevice("${string[0]}".toLowerCase().toByteArray(Charset.forName("UTF-8")))
    }

    companion object {
        const val TAG = "ArduinoProtocolSingleCh"
    }
}