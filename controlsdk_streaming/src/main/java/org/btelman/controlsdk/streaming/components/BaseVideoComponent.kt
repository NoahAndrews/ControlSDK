package org.btelman.controlsdk.streaming.components

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Component that will handle the core of the video streaming.
 *
 * Other classes will extend this for connectivity with specific integrations
 */
abstract class BaseVideoComponent : Component() {
    var streamInfo : StreamInfo? = null
    var processor : BaseVideoProcessor? = null
    var retriever : BaseVideoRetriever? = null

    override fun onInitializeComponent(applicationContext: Context?, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        bundle?.let {
            streamInfo = StreamInfo.fromBundle(it)
        } ?: throw IllegalArgumentException("Must use StreamInfo Bundle")
        processor = VideoProcessorFactory.findProcessor(bundle)
        processor ?: throw IllegalArgumentException("unable to resolve video processor")
    }

    override fun enableInternal() {

    }

    override fun disableInternal() {

    }

    override fun getType(): ComponentType {
        return ComponentType.STREAMING
    }
}