package org.btelman.controlsdk.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import org.btelman.controlsdk.BuildConfig
import org.btelman.controlsdk.R
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.interfaces.ComponentEventListener
import org.btelman.controlsdk.interfaces.IComponent
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.models.ComponentEventObject
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.controlsdk.utils.InlineBroadcastReceiver
import org.btelman.controlsdk.utils.NotificationUtil
import org.btelman.logutil.kotlin.LogUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * The main ControlSDK control service.
 * This handles the lifecycle and communication to components that come from outside the sdk
 */
class ControlSDKService : Service(), ComponentEventListener, Handler.Callback {
    private var running = false
    private val componentList = ArrayList<ComponentHolder<*>>()
    private val activeComponentList = ArrayList<IComponent>()
    private val logger = LogUtil("ControlSDKService")

    /**
     * Target we publish for clients to send messages to MessageHandler.
     */
    private lateinit var mMessenger: Messenger
    private var handlerThread : HandlerThread = HandlerThread("ControlSDK-main").also { it.start() }

    private val handler = Handler(handlerThread.looper, this)

    private var stopListenerReceiver: InlineBroadcastReceiver? = null

    override fun onCreate() {
        logger.d { "Starting ControlSDK : ${BuildConfig.VERSION_NAME} : ${BuildConfig.BUILD_TYPE}" }
        stopListenerReceiver = InlineBroadcastReceiver(SERVICE_STOP_BROADCAST){ _, _ ->
            logger.d { "Stopping service and calling System.exit(0)" }
            stopService()
            System.exit(0)
        }.also {
            logger.v { "Register stopListenerReceiver" }
            it.register(this)
        }
        logger.v { "Create NotificationUtil" }
        NotificationUtil.tryCreateNotificationChannel(this,
            getString(R.string.channel_name),
            getString(R.string.channel_description))
        setupForeground()

        logger.v { "Send Reset command to handler" }
        handler.obtainMessage(RESET).sendToTarget()
        mMessenger = Messenger(handler)
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    override fun onBind(intent: Intent): IBinder? {
        Toast.makeText(applicationContext, "binding", Toast.LENGTH_SHORT).show()
        logger.d { "Service bound" }
        emitState()
        return mMessenger.binder
    }

    override fun onRebind(intent: Intent?) {
        Toast.makeText(applicationContext, "rebinding", Toast.LENGTH_SHORT).show()
        logger.d { "Service re-bound" }
        emitState()
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Toast.makeText(applicationContext, "unbinding", Toast.LENGTH_SHORT).show()
        logger.d { "Service unbound" }
        return true
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        logger.d { "Service task removed" }
        stopService()
        super.onTaskRemoved(rootIntent)
    }

    /**
     * Message handler for Service level events, usually outside of the service
     */
    override fun handleMessage(msg: Message) : Boolean{
        when (msg.what) {
            START ->
                enable()
            STOP ->
                disable()
            ATTACH_COMPONENT -> {
                (msg.obj as? ComponentHolder<*>)?.let {
                    addToLifecycle(it)
                }
            }
            DETACH_COMPONENT -> {
                (msg.obj as? ComponentHolder<*>)?.let {
                    removeFromLifecycle(it)
                }
            }
            RESET -> {
                reset()
            }
            EVENT_BROADCAST ->{
                sendToComponents(msg)
            }
        }
        return false
    }

    /**
     * Message handler for components that we are controlling.
     * Best thing to do after is to push it to the service handler for processing,
     * as this could be from any thread
     */
    override fun handleMessage(eventObject: ComponentEventObject) {
        handler.obtainMessage(EVENT_BROADCAST, eventObject).sendToTarget()
    }

    /**
     * Send a message to all components. Each component may decide whether or not it should use it
     */
    private fun sendToComponents(msg: Message) {
        val obj = msg.obj as? ComponentEventObject
        var targetFilter : ComponentType? = null
        obj?.let {
            if((obj.source as? IComponent)?.getType() != obj.type){
                //send a message to all components of type obj.type
                targetFilter = obj.type
            }
        }

        logger.v { "send message to components of type $targetFilter : what=${msg.what}" }

        activeComponentList.forEach { component ->
            targetFilter?.takeIf { component.getType() != it }
                ?: component.dispatchMessage(msg)
        }
    }

    /**
     * Add a ComponentHolder to the service lifecycle. Will get instantiated into a Component when the service is enabled
     */
    private fun addToLifecycle(component: ComponentHolder<*>) {
        logger.v { "addToLifecycle : ${component.clazz.name}" }
        if(!componentList.contains(component))
            componentList.add(component)
    }

    /**
     * Remove a ComponentHolder from the service. Only takes affect once the service is reset at the moment
     */
    private fun removeFromLifecycle(component: ComponentHolder<*>) {
        logger.v { "removeFromLifecycle : ${component.clazz.name}" }
        componentList.remove(component)
    }

    /**
     * Instantiate all of the component holders on componentList and populate the cleared activeComponentList
     */
    private fun instantiateComponents() {
        logger.d { "instantiate components" }
        activeComponentList.clear()
        componentList.forEach { holder ->
            try {
                logger.v { "instantiate ${holder.clazz.name}" }
                val component = Component.instantiate(applicationContext, holder)
                activeComponentList.add(component)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * enable the components via co-routines. Calling this is only allowed via a co-routine,
     * and blocks the current thread
     * This prevents race conditions from happening between the UI and the service.
     * This also holds up any new messages until after all components are enabled
     */
    fun enable(){
        logger.d { "enable" }
        val componentListener : ComponentEventListener = this
        runBlocking {
            Toast.makeText(applicationContext, "Starting Control Service...", Toast.LENGTH_SHORT).show()
            instantiateComponents()
            val list = ArrayList<Deferred<Boolean>>()

            //enable all of our components
            activeComponentList.forEach{
                logger.v { "enabling ${it.javaClass.name} async" }
                it.setEventListener(componentListener)
                val deferred = it.enable()
                list.add(deferred) //add their deferred result to a list
            }

            //now wait for each one to complete
            list.forEach {
                it.await()
            }
            logger.v { "enabling classes done!" }
            setState(true)
        }
    }

    /**
     * Disables components, blocking the service messaging thread until complete
     */
    fun disable(){
        logger.d { "disable service" }
        Toast.makeText(applicationContext, "Stopping Control Service...", Toast.LENGTH_SHORT).show()
        runBlocking {
            activeComponentList.forEach{
                logger.v { "disabling ${it.javaClass.name}" }
                it.disable().await()
                it.setEventListener(null)
                logger.v { "disabling ${it.javaClass.name} done!" }
            }
            logger.v { "clear activeComponentList" }
            activeComponentList.clear()
            setState(false)
        }
    }

    /**
     * Reset the service. If running, we will disable, reload, then start again
     */
    private fun reset() {
        logger.d { "resetting" }
        if(running) {
            disable()
            reload()
            enable()
        }
        else{
            reload()
        }
    }

    /**
     * Reload the settings and prep for start
     */
    private fun reload() {
        logger.d { "reload" }
        componentList.clear()
    }

    /**
     * Stop the service properly, which involves running the disable command and then calling stopForeground
     */
    private fun stopService(){
        logger.d { "Stop Service" }
        if(running)
            runBlocking { disable() }
        logger.v { "Unregister stopListenerReceiver" }
        stopListenerReceiver?.unregister(this)

        logger.v { "stop foreground" }
        stopForeground(true)
        stopSelf()
    }

    /**
     * TODO remove? Part of older code system
     *
     * Set the current state and broadcast it to other classes within this app
     */
    private fun setState(value : Boolean){
        running = value
        emitState()
    }

    /**
     * Broadcast the current state of the app to anything listening inside of the app
     */
    private fun emitState() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
            Intent(SERVICE_STATUS_BROADCAST).also {
                logger.v { "emitting power state of $running" }
                it.putExtra("value", running)
            }
        )
    }

    /**
     * Setup the foreground service notification
     */
    private fun setupForeground() {
        logger.v { "Starting Foreground Service" }
        val intentHide = Intent(SERVICE_STOP_BROADCAST)
        intentHide.setPackage(packageName)
        val hide = PendingIntent.getBroadcast(this,
                System.currentTimeMillis().toInt(), intentHide, PendingIntent.FLAG_CANCEL_CURRENT)
        val notification = NotificationCompat.Builder(this, CONTROL_SERVICE)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.appBackgrounded))
                .addAction(R.drawable.ic_power_settings_new_black_24dp, getString(R.string.terminateApp), hide)
                .setSmallIcon(R.drawable.ic_settings_remote_black_24dp)
        startForeground(Random().nextInt(), notification.build())
    }

    companion object {
        var allowNotificationForExceptions = true
        const val START = 1
        const val STOP = 2
        const val RESET = 4
        const val ATTACH_COMPONENT = 5
        const val DETACH_COMPONENT = 6
        const val EVENT_BROADCAST = 7
        const val CONTROL_SERVICE = "control_service"
        const val SERVICE_STATUS_BROADCAST = "org.btelman.controlsdk.ServiceStatus"
        const val SERVICE_STOP_BROADCAST = "org.btelman.controlsdk.request.stop"
    }
}