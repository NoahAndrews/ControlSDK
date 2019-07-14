package org.btelman.control.sdk.demo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_demo.*
import org.btelman.controlsdk.enums.Operation
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.controlsdk.streaming.components.VideoComponent
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.tts.SystemDefaultTTSComponent
import org.btelman.controlsdk.viewModels.ControlSDKViewModel

class DemoActivity : AppCompatActivity() {

    private var recording = false
    private var controlSDKViewModel: ControlSDKViewModel? = null
    private val arrayList = ArrayList<ComponentHolder<*>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    private fun createComponentHolders() {
        val tts = ComponentHolder(SystemDefaultTTSComponent::class.java, null)
        val demoComponent = ComponentHolder(DemoComponent::class.java, null)
        val streamInfo = StreamInfo(
            "http://dev.remo.tv:1567/transmit?name=chan-eb194a7e-6a4f-4ae7-8112-b48a16032d91-video"
        )
        val videoComponent = ComponentHolder(VideoComponent::class.java, streamInfo.toBundle())
        arrayList.add(tts)
        arrayList.add(demoComponent)
        arrayList.add(videoComponent)
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
