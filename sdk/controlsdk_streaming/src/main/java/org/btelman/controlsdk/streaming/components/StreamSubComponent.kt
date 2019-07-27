package org.btelman.controlsdk.streaming.components

import android.content.Context
import androidx.annotation.CallSuper
import org.btelman.controlsdk.streaming.models.StreamInfo

/**
 * Class that all video or audio sub-components will use
 */
abstract class StreamSubComponent {
    protected var context: Context? = null

    /**
     * Enable the component. Due to the nature of storing context locally, force classes to call super
     */
    @CallSuper
    open fun enable(context: Context, streamInfo: StreamInfo){
        this.context = context
    }

    open fun disable(){

    }
}