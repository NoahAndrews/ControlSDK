package org.btelman.controlsdk.services

import android.content.*
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.btelman.controlsdk.enums.Operation
import org.btelman.controlsdk.interfaces.ControlSdkApi
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.logutil.kotlin.LogUtil

/**
 * Binder for ControlSDK Service that allows us to put all of the communication code in one class
 */
class ControlSDKServiceConnection private constructor(
        val context: Context
) : ServiceConnection, ControlSdkApi {

    private val log = LogUtil("ControlSDKServiceConnection", ControlSDKService.loggerID)
    /**
     * LiveData object for whether or not the service has the components enabled
     */
    private val serviceStateObserver: MutableLiveData<Operation> by lazy {
        MutableLiveData<Operation>()
    }

    /**
     * LiveData object for whether or not we have a valid connection to the service
     */
    private val serviceBoundObserver: MutableLiveData<Operation> by lazy {
        MutableLiveData<Operation>()
    }

    private var mService: Messenger? = null

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        // This is called when the connection with the service has been
        // established, giving us the object we can use to
        // interact with the service.  We are communicating with the
        // service using a Messenger, so here we get a client-side
        // representation of that from the raw IBinder object.
        mService = Messenger(service)
        serviceBoundObserver.postValue(Operation.OK)
        log.d{
            "onServiceConnected"
        }
    }

    override fun onServiceDisconnected(className: ComponentName) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        log.d{
            "onServiceDisconnected"
        }
        mService = null
        serviceBoundObserver.postValue(Operation.NOT_OK)
    }

    @Throws(IllegalStateException::class)
    override fun enable() {
        log.d{
            "enable"
        }
        serviceStateObserver.value = Operation.LOADING
        sendStateUnsafe(ControlSDKService.START)
    }

    @Throws(IllegalStateException::class)
    override fun disable(){
        log.d{
            "disable"
        }
        serviceStateObserver.value = Operation.LOADING
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

    override fun getServiceStateObserver(): LiveData<Operation> {
        return serviceStateObserver
    }

    override fun getServiceBoundObserver(): LiveData<Operation> {
        return serviceBoundObserver
    }

    private var receiver = Receiver(serviceStateObserver){ //onDisconnectRequest
        disconnectFromService()
    }

    override fun connectToService() {
        log.d{
            "connectToService"
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver,
                IntentFilter(ControlSDKService.SERVICE_STATUS_BROADCAST))
        Intent(context, ControlSDKService::class.java).also { intent ->
            context.bindService(intent, this, Context.BIND_ABOVE_CLIENT)
        }
    }

    override fun disconnectFromService() {
        log.d{
            "disconnectFromService"
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        context.unbindService(this)
    }

    class Receiver(val liveData: MutableLiveData<Operation>, val disconnectCallback : (() -> Unit)? = null) : BroadcastReceiver() {
        private val log = LogUtil("ControlSDKServiceConnectionReceiver", ControlSDKService.loggerID)
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when(it.action){
                    ControlSDKService.SERVICE_STATUS_BROADCAST -> {
                        log.d{
                            "onReceive ControlSDKService.SERVICE_STATUS_BROADCAST : ${it.getBooleanExtra("value", false)}"
                        }
                        setLiveData(it.getBooleanExtra("value", false))
                    }
                    ControlSDKService.SERVICE_STOP_BROADCAST -> {
                        log.d{
                            "onReceive ControlSDKService.SERVICE_STOP_BROADCAST"
                        }
                        disconnectCallback?.invoke()
                    }
                    else ->{/*do nothing*/}
                }
            }
        }

        private fun setLiveData(value: Boolean){
            liveData.value = if(value){
                Operation.OK
            }else{
                Operation.NOT_OK
            }
        }
    }

    companion object {
        fun getNewInstance(context: Context) : ControlSdkApi{
            return ControlSDKServiceConnection(context)
        }
    }
}

fun <T> LiveData<T>.observeAutoCreate(owner: LifecycleOwner, observer : (T) -> Unit) : Observer<T>{
    return Observer<T>{
        observer(it)
    }.also {
        observe(owner, it)
    }
}