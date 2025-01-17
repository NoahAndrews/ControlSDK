package org.btelman.controlsdk.hardware.drivers

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.widget.Toast
import org.btelman.controlsdk.enums.ComponentStatus
import org.btelman.controlsdk.hardware.drivers.libs.usb.UsbService
import org.btelman.controlsdk.hardware.interfaces.DriverComponent
import org.btelman.controlsdk.hardware.interfaces.HardwareDriver
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.logutil.kotlin.LogUtil
import java.lang.ref.WeakReference

/**
 * This is the communication component that interfaces with USBService.class (com.felhr.usbserial)
 */
@DriverComponent(description = "Send data over USB Serial at 9600 BAUD")
class FelhrUsbSerialDriver : HardwareDriver {
    private var componentStatus = ComponentStatus.DISABLED
    private val log = LogUtil("FelhrUsb", ControlSDKService.loggerID)

    override fun initConnection(context: Context) {
        setFilters(context)
        startService(context, UsbService::class.java, usbConnection, null) // Start UsbService(if it was not started before) and Bind it
    }

    override fun enable() {
        log.d("enable")
    }

    override fun disable() {
        log.d("disable")
    }

    override fun usesCustomSetup(): Boolean {
        return false
    }

    override fun clearSetup(context: Context) {
        //not used for USB
    }

    override fun needsSetup(activity: Activity): Boolean {
        return false
    }

    override fun setupComponent(activity: Activity, force : Boolean): Int {
        return -1
    }

    override fun receivedComponentSetupDetails(context: Context, intent: Intent?) {
        //ignore
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun send(byteArray: ByteArray): Boolean {
        usbService?.write(byteArray) //TODO actually get result?
        return true
    }

    override fun getStatus(): ComponentStatus {
        return componentStatus
    }

    override fun getAutoReboot(): Boolean {
        return false //Auto reboot useless, as we have to wait for something to connect to us
    }

    //Below is all USB Service code from com.felhr.usbservice
    //https://github.com/felHR85/UsbSerial/

    private fun startService(context: Context, service: Class<*>, serviceConnection: ServiceConnection, extras: Bundle?) {
        if (!UsbService.SERVICE_CONNECTED) {
            val startService = Intent(context, service)
            if (extras != null && !extras.isEmpty) {
                val keys = extras.keySet()
                for (key in keys) {
                    val extra = extras.getString(key)
                    startService.putExtra(key, extra)
                }
            }
            context.startService(startService)
        }
        val bindingIntent = Intent(context, service)
        context.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    //Some Intent filters for listening for USB events
    private fun setFilters(context: Context) {
        val filter = IntentFilter()
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED)
        filter.addAction(UsbService.ACTION_NO_USB)
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED)
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED)
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED)
        context.registerReceiver(mUsbReceiver, filter)
    }

    fun showMessageAndChangeState(context: Context, message: String?, status: ComponentStatus?){

        message?.let {
            log.d{
                message
            }
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        status?.let {
            componentStatus = it
        }
    }

    /**
    * Notifications from UsbService will be received here.
    */
    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbService.ACTION_USB_PERMISSION_GRANTED -> showMessageAndChangeState(
                        context, "USB Ready", ComponentStatus.STABLE)
                UsbService.ACTION_USB_PERMISSION_NOT_GRANTED -> showMessageAndChangeState(
                        context, "USB Permission not granted", ComponentStatus.ERROR)
                UsbService.ACTION_NO_USB -> showMessageAndChangeState(
                        context, "No USB connected", null)
                UsbService.ACTION_USB_DISCONNECTED -> showMessageAndChangeState(
                        context, "USB disconnected", ComponentStatus.ERROR)
                UsbService.ACTION_USB_NOT_SUPPORTED -> showMessageAndChangeState(
                        context, "USB device not supported", ComponentStatus.ERROR)
            }
        }
    }
    private var usbService: UsbService? = null
    private var mHandler: MyHandler? = null
    private val usbConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, arg1: IBinder) {
            usbService = (arg1 as UsbService.UsbBinder).service
            usbService!!.setHandler(mHandler)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            usbService = null
        }
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private class MyHandler(activity: Context) : Handler() {
        private val log = LogUtil("FelhrUsbHandler", ControlSDKService.loggerID)
        private val mActivity: WeakReference<Context> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val message : String? = when (msg.what) {
                UsbService.MESSAGE_FROM_SERIAL_PORT -> {
                    val data = msg.obj as String
                    log.v{
                        "handleMessage : $data"
                    }
                    null
                }
                UsbService.CTS_CHANGE -> "CTS_CHANGE"
                UsbService.DSR_CHANGE -> "DSR_CHANGE"
                else ->{
                    null
                }
            }
            message?.let{
                log.d{
                    "handleMessage : $it"
                }
            }
        }
    }
}