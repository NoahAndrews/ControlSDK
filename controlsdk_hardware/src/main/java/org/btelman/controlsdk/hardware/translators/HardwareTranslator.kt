package org.btelman.controlsdk.hardware.translators

abstract class HardwareTranslator {
    abstract fun translateString(command: String) : ByteArray
    abstract fun translateAny(command: Any) : ByteArray
    abstract fun translateStop() : ByteArray

    companion object{
        const val TAG = "HardwareTranslator"
    }
}