package org.btelman.controlsdk.hardware.enums

import android.content.Context
import org.btelman.controlsdk.hardware.protocols.ArduinoSendSingleCharProtocol
import org.btelman.controlsdk.hardware.protocols.ArduinoTranslator
import org.btelman.controlsdk.hardware.protocols.NXTJoystickDriverProtocol
import org.btelman.controlsdk.hardware.protocols.SingleByteProtocol
import org.btelman.controlsdk.hardware.translators.HardwareTranslator

/**
 * protocol types will reside in here
 */
enum class ProtocolType {
    /**
     * Sends raw commands to Arduino, this will appear in the form of 'f', 'b', 'stop'
     */
    ArduinoRaw,
    /**
     * Single byte control. Can control SaberTooth devices through simplified mode or other devices
     */
    SingleByte,
    /**
     * Sends a single char instead of line ended text. This will appear in the form of 'f', 'b', 's' for stop
     */
    ArduinoSingleChar,
    /**
     * Sends commands to a Lego Mindstorms NXT using the Tetrix/Matrix Controller driver
     */
    NXTJoystickDriver;


    fun getInstantiatedClass(context: Context) : HardwareTranslator {
        return when(this){
            ArduinoRaw -> ArduinoTranslator()
            SingleByte -> SingleByteProtocol()
            ArduinoSingleChar -> ArduinoSendSingleCharProtocol()
            NXTJoystickDriver -> NXTJoystickDriverProtocol()
        }
    }
}