package org.btelman.controlsdk.hardware.translators

import org.btelman.controlsdk.hardware.interfaces.Translator
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.logutil.kotlin.LogUtil
import java.nio.charset.Charset

/**
 * Arduino String sending converter
 * Sends output string in format of "$string\r\n"
 */
@TranslatorComponent(description = "Sends output String in lowercase format of \"{string}\\r\\n\" as a ByteArray")
class ArduinoTranslator : Translator {
    private val log = LogUtil("ArduinoTranslator", ControlSDKService.loggerID)
    override fun translateString(command: String): ByteArray {
        return getBytesArrayWithTerminator(command)
    }

    override fun translateAny(command: Any): ByteArray {
        TODO()
    }

    override fun translateStop(): ByteArray {
        return getBytesArrayWithTerminator("stop")
    }

    private fun getBytesArrayWithTerminator(string : String) : ByteArray{
        val messageWithTerminator = "$string\r\n"
        log.v{
            "getBytesArrayWithTerminator : $messageWithTerminator"
        }
        return messageWithTerminator.toLowerCase().toByteArray(Charset.forName("UTF-8"))
    }

    companion object {
        const val TAG = "ArduinoProtocol"
    }
}