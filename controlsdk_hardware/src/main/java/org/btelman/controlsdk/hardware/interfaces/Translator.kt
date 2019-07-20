package org.btelman.controlsdk.hardware.interfaces

interface Translator {
    fun translateString(command: String) : ByteArray
    fun translateAny(command: Any) : ByteArray
    fun translateStop() : ByteArray
}