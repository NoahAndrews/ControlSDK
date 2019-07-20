package org.btelman.control.sdk.demo

import android.os.Message
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component

/**
 * Loop through the FBLR controls to move around
 */
class DummyController : Component(){

    override fun enableInternal() {
        dispatchMessage(Message.obtain(handler, EVENT_MAIN))
    }

    override fun disableInternal() {

    }

    var i = 0

    override fun handleMessage(message: Message): Boolean {
        if(message.what == EVENT_MAIN){
            val string = when(i%4){
                0 -> {
                    "f"
                }
                1 -> {
                    "b"
                }
                2 -> {
                    "r"
                }
                3 -> {
                    "l"
                }
                else ->{
                    i = 1
                    "stop"
                }
            }
            i++
            eventDispatcher?.handleMessage(ComponentType.HARDWARE, EVENT_MAIN, string, this)
        }
        dispatchMessage(Message.obtain(handler, EVENT_MAIN))
        return super.handleMessage(message)
    }

    override fun getType(): ComponentType {
        return ComponentType.CUSTOM
    }
}