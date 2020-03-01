package org.btelman.controlsdk.hardware.components

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.runBlocking
import org.btelman.controlsdk.enums.ComponentStatus
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.hardware.interfaces.Translator
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.models.ComponentEventObject
import org.btelman.controlsdk.utils.BundleUtil

/**
 * Component that receives messages and handles sending them to actual hardware
 */
class HardwareComponent : Component() {
    var translatorComponent : Translator? = null
    var communicationDriverComponent : CommunicationDriverComponent? = null

    override fun onInitializeComponent(applicationContext: Context, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        bundle ?: return
        BundleUtil.getClassFromBundle(bundle, HARDWARE_TRANSLATOR_BUNDLE_ID)?.let {
            translatorComponent = BundleUtil.checkForAndInitClass(it, Translator::class.java)
        }
        communicationDriverComponent = BundleUtil.getClassFromBundle(bundle, HARDWARE_DRIVER_BUNDLE_ID)?.let {
            BundleUtil.checkForAndInitClass(it, CommunicationDriverComponent::class.java)
        } ?: CommunicationDriverComponent()
        translatorComponent ?: return
        communicationDriverComponent?.onInitializeComponent(applicationContext, bundle)
        communicationDriverComponent?.setEventListener(eventDispatcher)
    }

    override fun enableInternal() {
        runBlocking{
            communicationDriverComponent?.enable()?.await()
        }
        status = ComponentStatus.STABLE
    }

    override fun disableInternal() {
        runBlocking{
            communicationDriverComponent?.disable()?.await()
        }
    }

    override fun handleExternalMessage(message: ComponentEventObject): Boolean {
        if(message.type == ComponentType.HARDWARE){
            when(message.what){
                EVENT_MAIN -> {
                    when(val data = message.data){
                        is String ->{
                            translatorComponent?.translateString(data)?.let { packet ->
                                communicationDriverComponent?.sendToDriver(packet)
                            }
                        }
                        is Any -> {
                            translatorComponent?.translateAny(data)?.let { packet ->
                                communicationDriverComponent?.sendToDriver(packet)
                            }
                        }
                    }
                }
            }
        }
        return super.handleExternalMessage(message)
    }

    override fun getType(): ComponentType {
        return ComponentType.HARDWARE
    }

    companion object{
        const val HARDWARE_DRIVER_BUNDLE_ID = "org.btelman.controlsdk.hardware.driver"
        const val HARDWARE_TRANSLATOR_BUNDLE_ID = "org.btelman.controlsdk.hardware.translator"
    }
}