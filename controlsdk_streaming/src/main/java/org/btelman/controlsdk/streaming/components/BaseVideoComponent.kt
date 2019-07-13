package org.btelman.controlsdk.streaming.components

import android.content.Context
import android.os.Bundle
import android.os.Message
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.streaming.factories.VideoProcessorFactory
import org.btelman.controlsdk.streaming.factories.VideoRetrieverFactory
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor
import org.btelman.controlsdk.streaming.video.retrievers.BaseVideoRetriever

/**
 * Component that will handle the core of the video streaming.
 *
 * Other classes will extend this for connectivity with specific integrations
 */
abstract class BaseVideoComponent : Component() {
    lateinit var streamInfo : StreamInfo
    lateinit var processor : BaseVideoProcessor
    lateinit var retriever : BaseVideoRetriever

    override fun onInitializeComponent(applicationContext: Context?, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        bundle?.let {
            streamInfo = StreamInfo.fromBundle(it) ?: throw IllegalArgumentException("Must use StreamInfo Bundle")
        } ?: throw IllegalArgumentException("Must use StreamInfo Bundle")
        processor = VideoProcessorFactory.findProcessor(bundle) ?: throw IllegalArgumentException("unable to resolve video processor")
        retriever = VideoRetrieverFactory.findRetriever(bundle) ?: throw IllegalArgumentException("unable to resolve video retriever")
    }

    override fun enableInternal() {
        processor.enable(context?.get()!!, streamInfo)
        retriever.enable(streamInfo)
        push(DO_FRAME)
    }

    override fun disableInternal() {
        processor.disable()
        retriever.disable()
    }

    override fun handleMessage(message: Message): Boolean {
        when(message.what){
            DO_FRAME -> updateLoop()
            FRAME_FETCH -> fetchFrame()
        }
        return super.handleMessage(message)
    }

    open fun fetchFrame() {
        processor.processData(retriever.grabImageData())
        push(DO_FRAME)
    }

    open fun updateLoop(){
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