package org.btelman.controlsdk.streaming.audio.retrievers

import org.btelman.controlsdk.streaming.components.StreamSubComponent

/**
 * Created by Brendon on 7/14/2019.
 */
abstract class BaseAudioRetriever : StreamSubComponent(){
    abstract fun retrieveAudioByteArray() : ByteArray?
}