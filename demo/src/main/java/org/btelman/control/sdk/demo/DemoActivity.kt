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
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.controlsdk.streaming.components.AudioComponent
import org.btelman.controlsdk.streaming.components.VideoComponent
import org.btelman.controlsdk.streaming.models.CameraDeviceInfo
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.tts.SystemDefaultTTSComponent
import org.btelman.controlsdk.viewModels.ControlSDKViewModel

class DemoActivity : AppCompatActivity() {

    private var request: Int = -1
    private var recording = false
    private var controlSDKViewModel: ControlSDKViewModel? = null
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

        controlSDKViewModel = ControlSDKViewModel.getObject(this)
        controlSDKViewModel?.setServiceBoundListener(this){ connected ->
            powerButton.isEnabled = connected == Operation.OK
        }
        controlSDKViewModel?.setStatusObserver(this){ serviceStatus ->
            powerButton?.let {
                powerButton.setTextColor(parseColorForOperation(serviceStatus))
                val isLoading = serviceStatus == Operation.LOADING
                powerButton.isEnabled = !isLoading
                if(isLoading) return@setStatusObserver //processing command. Disable button
                recording = serviceStatus == Operation.OK
                /*if(recording && settings.autoHideMainControls.value)
                    startSleepDelayed()*/
            }
        }

        powerButton?.setOnClickListener {
            when(controlSDKViewModel?.api?.getServiceStateObserver()?.value){
                Operation.NOT_OK -> {
                    arrayList.forEach {
                        controlSDKViewModel?.api?.attachToLifecycle(it)
                    }
                    controlSDKViewModel?.api?.enable()
                }
                Operation.LOADING -> {} //do nothing
                Operation.OK -> {
                    arrayList.forEach {
                        controlSDKViewModel?.api?.detachFromLifecycle(it)
                    }
                    controlSDKViewModel?.api?.disable()
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

        if(bt.needsSetup(this))
            request = bt.setupComponent(this, true)
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
            "http://dev.remo.tv:1567/transmit?name=chan-eb194a7e-6a4f-4ae7-8112-b48a16032d91-video",
            "http://dev.remo.tv:1567/transmit?name=chan-eb194a7e-6a4f-4ae7-8112-b48a16032d91-audio"
            ,deviceInfo = CameraDeviceInfo.fromCamera(0)
        )
        val bundle = Bundle()
        streamInfo.addToExistingBundle(bundle)
        //VideoRetrieverFactory.putClassInBundle(DummyCanvasRetriever::class.java, bundle)
        //VideoProcessorFactory.putClassInBundle(DummyVideoProcessor::class.java, bundle)
        val videoComponent = ComponentHolder(VideoComponent::class.java, bundle)
        val audioComponent = ComponentHolder(AudioComponent::class.java, bundle)

        val hardwareBundle = Bundle()
        hardwareBundle.putSerializable(HardwareDriver.BUNDLE_ID, BluetoothClassicDriver::class.java)
        hardwareBundle.putSerializable(HardwareComponent.HARDWARE_TRANSLATOR_BUNDLE_ID, ArduinoSendSingleCharTranslator::class.java)
        val hardwareComponent = ComponentHolder(HardwareComponent::class.java, hardwareBundle)
        val dummyComponent = ComponentHolder(DummyController::class.java, Bundle())
        arrayList.add(tts)
        arrayList.add(demoComponent)
        arrayList.add(videoComponent)
        arrayList.add(audioComponent)
        arrayList.add(hardwareComponent)
        arrayList.add(dummyComponent)
        arrayList.add(ComponentHolder(UnstableComponent::class.java, Bundle()))
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
