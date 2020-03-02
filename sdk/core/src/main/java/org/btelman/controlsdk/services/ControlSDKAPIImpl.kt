package org.btelman.controlsdk.services

import android.os.Message
import android.os.Messenger
import org.btelman.controlsdk.interfaces.ControlSDKMessenger
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.logutil.kotlin.LogUtil

/**
 * Created by Brendon on 3/1/2020.
 */
open class ControlSdkWrapper : ControlSDKMessenger{
    private val log = LogUtil("ControlSDKAPIImpl", ControlSDKService.loggerID)
    private var mService : Messenger? = null

    override fun onMessenger(messenger: Messenger?) {
        mService = messenger
    }

    @Throws(IllegalStateException::class)
    override fun enable() {
        log.d{
            "enable"
        }
        sendStateUnsafe(ControlSDKService.START)
    }

    @Throws(IllegalStateException::class)
    override fun disable(){
        log.d{
            "disable"
        }
        sendStateUnsafe(ControlSDKService.STOP)
    }

    @Throws(IllegalStateException::class)
    override fun reset() {
        log.d{
            "reset"
        }
        sendStateUnsafe(ControlSDKService.RESET)
    }

    override fun attachToLifecycle(component: ComponentHolder<*>) {
        log.v{
            "attachToLifecycle ${component.clazz.name}"
        }
        sendStateUnsafe(ControlSDKService.ATTACH_COMPONENT, component)
    }

    override fun detachFromLifecycle(component: ComponentHolder<*>) {
        log.v{
            "detachFromLifecycle ${component.clazz.name}"
        }
        sendStateUnsafe(ControlSDKService.DETACH_COMPONENT, component)
    }

    @Throws(IllegalStateException::class)
    private fun sendStateUnsafe(what : Int, obj : Any? = null) {
        val message = obj?.let {
            Message.obtain(null, what, obj)
        } ?: Message.obtain(null, what)
        mService?.send(message) ?: throw IllegalStateException()
    }
}