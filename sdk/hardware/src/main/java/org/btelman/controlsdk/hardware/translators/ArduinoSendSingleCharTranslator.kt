package org.btelman.controlsdk.hardware.translators

import org.btelman.controlsdk.hardware.interfaces.Translator
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import java.nio.charset.Charset

/**
 * Sends the first character of desired command without sending /r/n
 *
 * Added in case somebody runs into a board that cannot properly handle parsing a multi character command
 */
@TranslatorComponent
class ArduinoSendSingleCharTranslator : Translator {
    override fun translateString(command: String): ByteArray {
        return getFirstCharAsByteArray(command)
    }

    override fun translateAny(command: Any): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translateStop(): ByteArray {
        return getFirstCharAsByteArray("s")
    }

    private fun getFirstCharAsByteArray(string : String) : ByteArray{
        return "${string[0]}".toLowerCase().toByteArray(Charset.forName("UTF-8"))
    }

    companion object {
        const val TAG = "ArduinoProtocolSingleCh"
    }
}