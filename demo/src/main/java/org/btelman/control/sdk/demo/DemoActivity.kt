package org.btelman.control.sdk.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_demo.*
import org.btelman.controlsdk.enums.Operation
import org.btelman.controlsdk.hardware.components.HardwareComponent
import org.btelman.controlsdk.hardware.drivers.BluetoothClassicDriver
import org.btelman.controlsdk.hardware.interfaces.DriverComponent
import org.btelman.controlsdk.hardware.interfaces.HardwareDriver
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import org.btelman.controlsdk.hardware.translators.ArduinoSendSingleCharTranslator
import org.btelman.controlsdk.hardware.utils.HardwareFinder
import org.btelman.controlsdk.interfaces.ControlSdkApi
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.controlsdk.services.ControlSDKServiceConnection
import org.btelman.controlsdk.services.observeAutoCreate
import org.btelman.controlsdk.streaming.components.AudioComponent
import org.btelman.controlsdk.streaming.components.VideoComponent
import org.btelman.controlsdk.streaming.factories.VideoRetrieverFactory
import org.btelman.controlsdk.streaming.models.CameraDeviceInfo
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.video.retrievers.api16.Camera1SurfaceTextureComponent
import org.btelman.controlsdk.tts.SystemDefaultTTSComponent

class DemoActivity : AppCompatActivity() {

    private var request: Int = -1
    private var recording = false
    private var controlAPI : ControlSdkApi = ControlSDKServiceConnection.getNewInstance(this)
    private val arrayList = ArrayList<ComponentHolder<*>>()
    val bt = BluetoothClassicDriver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        controlAPI.getServiceBoundObserver().observeAutoCreate(this){ connected ->
            handleServiceBoundState(connected)
        }
        controlAPI.getServiceStateObserver().observeAutoCreate(this){ serviceStatus ->
            handleServiceState(serviceStatus)
        }
        controlAPI.connectToService()

        powerButton?.setOnClickListener {
            when(controlAPI.getServiceStateObserver().value){
                Operation.NOT_OK -> {
                    arrayList.forEach {
                        controlAPI.attachToLifecycle(it)
                    }
                    controlAPI.enable()
                }
                Operation.LOADING -> {} //do nothing
                Operation.OK -> {
                    arrayList.forEach {
                        controlAPI.detachFromLifecycle(it)
                    }
                    controlAPI.disable()
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
        controlAPI.disconnectFromService()
        super.onDestroy()
    }

    private fun handleServiceBoundState(connected: Operation) {
        powerButton.isEnabled = connected == Operation.OK
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
            "http://dev.remo.tv:1567/transmit?name=chan-3650a36e-a336-4531-9084-26cee4a33c02-video", //TODO video url
            deviceInfo = CameraDeviceInfo.fromCamera(0)
        )
        val bundle = Bundle()
        streamInfo.addToExistingBundle(bundle)
        //VideoRetrieverFactory.putClassInBundle(DummyCanvasRetriever::class.java, bundle)
        //VideoProcessorFactory.putClassInBundle(DummyVideoProcessor::class.java, bundle)
        VideoRetrieverFactory.putClassInBundle(Camera1SurfaceTextureComponent::class.java, bundle)
        val videoComponent = ComponentHolder(VideoComponent::class.java, bundle)
        //val audioComponent = ComponentHolder(AudioComponent::class.java, bundle)

        val hardwareBundle = Bundle()
        hardwareBundle.putSerializable(HardwareDriver.BUNDLE_ID, BluetoothClassicDriver::class.java)
        hardwareBundle.putSerializable(HardwareComponent.HARDWARE_TRANSLATOR_BUNDLE_ID, ArduinoSendSingleCharTranslator::class.java)
        val hardwareComponent = ComponentHolder(HardwareComponent::class.java, hardwareBundle)
        val dummyComponent = ComponentHolder(DummyController::class.java, Bundle())
        arrayList.add(tts) //noisy and potentially annoying due to dummyComponent giving it garbage, but good for testing
        //arrayList.add(demoComponent)
        arrayList.add(videoComponent)
        //arrayList.add(audioComponent)
        //arrayList.add(hardwareComponent)
        //arrayList.add(dummyComponent)
        //arrayList.add(ComponentHolder(UnstableComponent::class.java, Bundle()))
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
