package org.btelman.controlsdk.streaming.components

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.streaming.factories.VideoProcessorFactory
import org.btelman.controlsdk.streaming.factories.VideoRetrieverFactory
import org.btelman.controlsdk.streaming.video.processors.BaseVideoProcessor
import org.btelman.controlsdk.streaming.video.retrievers.BaseVideoRetriever

/**
 * Component that will handle the core of the video streaming.
 *
 * Other classes will extend this for connectivity with specific integrations
 */
open class VideoComponent : StreamComponent<BaseVideoRetriever, BaseVideoProcessor>()  {

    override fun onInitializeComponent(applicationContext: Context?, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        bundle!!
        processor = VideoProcessorFactory.findProcessor(bundle) ?: throw IllegalArgumentException("unable to resolve video processor")
        retriever = VideoRetrieverFactory.findRetriever(bundle) ?: throw IllegalArgumentException("unable to resolve video retriever")
    }

    override fun doWorkLoop() {
        retriever.grabImageData()?.let {
            processor.processData(it)
        }
    }
}