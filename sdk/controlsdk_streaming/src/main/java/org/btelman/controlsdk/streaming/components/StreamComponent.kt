package org.btelman.controlsdk.streaming.components

import android.content.Context
import android.os.Bundle
import android.os.Message
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.streaming.models.StreamInfo


/**
 * Created by Brendon on 7/14/2019.
 */
abstract class StreamComponent<R : StreamSubComponent,P : StreamSubComponent> : Component() {
    protected lateinit var streamInfo : StreamInfo
    protected lateinit var processor : P
    protected lateinit var retriever : R

    override fun onInitializeComponent(applicationContext: Context, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        bundle?.let {
            streamInfo = StreamInfo.fromBundle(it) ?: throw IllegalArgumentException("Must use StreamInfo Bundle")
        } ?: throw IllegalArgumentException("Must use StreamInfo Bundle")
    }

    override fun enableInternal() {
        processor.enable(context!!, streamInfo)
        retriever.enable(context!!, streamInfo)
        push(DO_FRAME)
    }

    override fun disableInternal() {
        retriever.disable()
        processor.disable()
    }

    override fun handleMessage(message: Message): Boolean {
        when(message.what){
            DO_FRAME -> updateLoop()
            FRAME_FETCH -> fetchFrame()
        }
        return super.handleMessage(message)
    }

    fun fetchFrame() {
        doWorkLoop()
        push(DO_FRAME)
    }

    abstract fun doWorkLoop()

    fun updateLoop(){
        push(FRAME_FETCH)
    }

    override fun getType(): ComponentType {
        return ComponentType.STREAMING
    }

    fun push(what : Int, obj : Any? = null){
        if(!handler.hasMessages(what)) {
            val message = obj?.let {
                handler.obtainMessage(what, it)
            } ?: handler.obtainMessage(what)
            message.sendToTarget()
        }
    }

    companion object{
        private const val FRAME_FETCH = 0
        private const val FRAME_PUSH = 1
        private const val DO_FRAME = 2
    }
}