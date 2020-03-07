package org.btelman.control.sdk.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_demo.*
import org.btelman.android.ffmpeg.FFmpegRunner
import org.btelman.controlsdk.enums.Operation
import org.btelman.controlsdk.hardware.components.HardwareComponent
import org.btelman.controlsdk.hardware.drivers.BluetoothClassicDriver
import org.btelman.controlsdk.hardware.drivers.FelhrUsbSerialDriver
import org.btelman.controlsdk.hardware.interfaces.DriverComponent
import org.btelman.controlsdk.hardware.interfaces.HardwareDriver
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import org.btelman.controlsdk.hardware.translators.ArduinoSendSingleCharTranslator
import org.btelman.controlsdk.hardware.utils.HardwareFinder
import org.btelman.controlsdk.interfaces.ControlSdkServiceWrapper
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.controlsdk.services.ControlSDKServiceConnection
import org.btelman.controlsdk.services.observeAutoCreate
import org.btelman.controlsdk.streaming.components.AudioComponent
import org.btelman.controlsdk.streaming.components.VideoComponent
import org.btelman.controlsdk.streaming.models.CameraDeviceInfo
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.tts.SystemDefaultTTSComponent
import org.btelman.logutil.kotlin.LogLevel
import org.btelman.logutil.kotlin.LogUtil
import org.btelman.logutil.kotlin.LogUtilInstance

class DemoActivity : AppCompatActivity() {

    private var request: Int = -1
    private var recording = false
    private var controlServiceWrapper : ControlSdkServiceWrapper = ControlSDKServiceConnection.getNewInstance(this)
    private val arrayList = ArrayList<ComponentHolder<*>>()
    private val listeners = ArrayList<ComponentHolder<*>>()
    val bt = BluetoothClassicDriver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtilInstance(ControlSDKService.CONTROL_SERVICE, LogLevel.VERBOSE).also {
            Log.d("DemoActivity", "Setup logger")
            LogUtil.addCustomLogUtilInstance(ControlSDKService::class.java.name, it)
        }
        if(!FFmpegRunner.checkIfUpToDate(this))
            FFmpegRunner.update(this)
        //examples of fetching all available services
        HardwareFinder.getTranslationClasses(this).forEach{
            Log.d("TRANSLATE", it.name+"\n" +
                    it.getAnnotation(TranslatorComponent::class.java)?.description)
        }
        HardwareFinder.getDriverClasses(this).forEach{
            Log.d("DRIVER", it.name+"\n" +
                    it.getAnnotation(DriverComponent::class.java)?.description)
        }

        setContentView(R.layout.activity_demo)
        createComponentHolders()

        ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, ControlSDKService::class.java))

        controlServiceWrapper.getServiceBoundObserver().observeAutoCreate(this){ connected ->
            //Note that we do not add this to the arraylist. Instead we add it to the service right away
            handleServiceBoundState(connected)
        }
        controlServiceWrapper.getServiceStateObserver().observeAutoCreate(this){ serviceStatus ->
            handleServiceState(serviceStatus)
        }
        controlServiceWrapper.connectToService()

        powerButton?.setOnClickListener {
            when(controlServiceWrapper.getServiceStateObserver().value){
                Operation.NOT_OK -> {
                    arrayList.forEach {
                        controlServiceWrapper.attachToLifecycle(it)
                    }
                    controlServiceWrapper.enable()
                }
                Operation.LOADING -> {} //do nothing
                Operation.OK -> {
                    arrayList.forEach {
                        controlServiceWrapper.detachFromLifecycle(it)
                    }
                    controlServiceWrapper.disable()
                }
                null -> powerButton.setTextColor(parseColorForOperation(null))
            }
        }

        settingsButton?.setOnClickListener {
            Intent(this, DemoSettingsActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        powerButton.setOnLongClickListener{
            request = bt.setupComponent(this, true)
            true
        }
    }

    override fun onDestroy() {
        controlServiceWrapper.disconnectFromService()
        listeners.forEach {
            controlServiceWrapper.removeListenerOrController(it)
        }
        super.onDestroy()
    }

    private fun handleServiceBoundState(connected: Operation) {
        powerButton.isEnabled = connected == Operation.OK
        if(connected == Operation.OK){
            listeners.forEach {
                controlServiceWrapper.addListenerOrController(it)
            }
        }
        else if(connected == Operation.NOT_OK){
            listeners.forEach {
                controlServiceWrapper.removeListenerOrController(it)
            }
        }
    }

    private fun handleServiceState(serviceStatus: Operation) {
        powerButton?.let {
            powerButton.setTextColor(parseColorForOperation(serviceStatus))
            val isLoading = serviceStatus == Operation.LOADING
            powerButton.isEnabled = !isLoading
            if(isLoading) return //processing command. Disable button
            recording = serviceStatus == Operation.OK
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Check if result was due to a pending interface setup
        if(request == requestCode && resultCode == Activity.RESULT_OK){
            bt.receivedComponentSetupDetails(this, data)
        }
    }

    private fun createComponentHolders() {
        val tts = ComponentHolder(SystemDefaultTTSComponent::class.java, null)
        val demoComponent = ComponentHolder(DemoComponent::class.java, null)
        val streamInfo = StreamInfo(
            "http://remo.tv:1567/transmit?name=chan-9c1d2981-4990-43ff-bd12-7a95ed221743-video", //TODO video url
            "http://remo.tv:1567/transmit?name=chan-9c1d2981-4990-43ff-bd12-7a95ed221743-audio",
            width = 1280,
            height = 720,
            deviceInfo = CameraDeviceInfo.fromCamera(0)
        )
        val bundle = Bundle()
        streamInfo.addToExistingBundle(bundle)
        //VideoRetrieverFactory.putClassInBundle(DummyCanvasRetriever::class.java, bundle)
        //VideoProcessorFactory.putClassInBundle(DummyVideoProcessor::class.java, bundle)
        //VideoRetrieverFactory.putClassInBundle(Camera1SurfaceTextureComponent::class.java, bundle)
        val videoComponent = ComponentHolder(VideoComponent::class.java, bundle)
        val audioComponent = ComponentHolder(AudioComponent::class.java, bundle)

        val hardwareBundle = Bundle()
        hardwareBundle.putSerializable(HardwareDriver.BUNDLE_ID, FelhrUsbSerialDriver::class.java)
        hardwareBundle.putSerializable(HardwareComponent.HARDWARE_TRANSLATOR_BUNDLE_ID, ArduinoSendSingleCharTranslator::class.java)
        val hardwareComponent = ComponentHolder(HardwareComponent::class.java, hardwareBundle)
        val dummyComponent = ComponentHolder(DummyController::class.java, Bundle())
        arrayList.add(tts) //noisy and potentially annoying due to dummyComponent giving it garbage, but good for testing
        arrayList.add(demoComponent)
        arrayList.add(videoComponent)
        arrayList.add(audioComponent)
        arrayList.add(hardwareComponent)
        arrayList.add(dummyComponent)
        arrayList.add(ComponentHolder(UnstableComponent::class.java, Bundle()))

        listeners.add(DummyListener.createHolder())
    }

    fun parseColorForOperation(state : Operation?) : Int{
        val color : Int = when(state){
            Operation.OK -> Color.GREEN
            Operation.NOT_OK -> Color.RED
            Operation.LOADING -> Color.YELLOW
            null -> Color.CYAN
            else -> Color.BLACK
        }
        return color
    }
}
