package org.btelman.controlsdk.streaming.models

data class AudioPacket(val b : ByteArray,
                       val timecode : Long = System.currentTimeMillis()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AudioPacket

        if (!b.contentEquals(other.b)) return false
        if (timecode != other.timecode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = b.contentHashCode()
        result = 31 * result + timecode.hashCode()
        return result
    }

}