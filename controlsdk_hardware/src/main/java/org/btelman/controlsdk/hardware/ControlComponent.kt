package org.btelman.controlsdk.hardware

import android.content.Context
import android.util.Log
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.models.ComponentEventObject

/**
 *  Base robot control component
 *  Extend from this to hook up to the core interface properly
 *
 *  Subscribes to EventManager.COMMAND and EventManager.STOP_EVENT automatically
 */
abstract class ControlComponent(context: Context) : Component(context){

    override fun getType(): ComponentType {
        return ComponentType.HARDWARE
    }

    /**
     * Called when any command is received, including but not limited to strings
     */
    open fun onCommand(command : Any?){Log.d(TAG, "onCommand")}

    /**
     * Called when a command is received, and is a non-null String
     */
    open fun onStringCommand(command : String){Log.d(TAG, "onStringCommand $command")}
    open fun onStop(any : Any?){Log.d(TAG, "onStop")}

    override fun enableInternal(){
        Log.d(TAG, "enable")
    }

    override fun disableInternal(){
        Log.d(TAG, "disable")
    }

    override fun timeout() {
        super.timeout()
        onStopInternal(null)
    }

    override fun handleExternalMessage(message: ComponentEventObject): Boolean {
        if(message.type == ComponentType.CONTROL_SOCKET){
            when(message.what){
                EVENT_MAIN -> onCommandInternal(message.data)
            }
        }
        return super.handleExternalMessage(message)
    }

    private fun onCommandInternal(command : Any?){
        (command as? String)?.let{it ->
            onStringCommand(it)
        }
        onCommand(command)
    }

    private val onStopInternal : (Any?) -> Unit  = {
        onStop(it)
    }

    /**
     * Send message out to device though our event manager.
     */
    fun sendToDevice(byteArray: ByteArray?){
        byteArray?.let {
            Log.d("ControlComponent","sendToDevice")
            eventDispatcher?.handleMessage(getType(), EVENT_MAIN, byteArray, this)
        }
    }

    companion object {
        const val TAG = "ControlComponent"
    }
}