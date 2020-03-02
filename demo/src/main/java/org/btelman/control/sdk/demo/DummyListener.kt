package org.btelman.control.sdk.demo

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.enums.ComponentStatus
import org.btelman.controlsdk.enums.ServiceStatus
import org.btelman.controlsdk.interfaces.IListener
import org.btelman.controlsdk.models.ComponentHolder
import org.btelman.controlsdk.services.ControlSDKService
import org.btelman.logutil.kotlin.LogUtil


class DummyListener : IListener {
    private val log = LogUtil("DummyListener", ControlSDKService.loggerID)

    override fun onInitializeListener(context: Context, bundle: Bundle?) {
        super.onInitializeListener(context, bundle)
        if(bundle?.getString("test", null) != "test")
            log.e("Bundle does not contain test value")
    }

    override fun onServiceStateChange(status: ServiceStatus) {
        super.onServiceStateChange(status)
        log.d{
            "SERVICE_STATUS = $status"
        }
    }

    override fun onComponentStatus(clazz: Class<*>, componentStatus: ComponentStatus) {
        super.onComponentStatus(clazz, componentStatus)
        log.d{
            "COMPONENT_STATUS ${clazz.name}=$componentStatus"
        }
    }

    override fun onComponentAdded(component: ComponentHolder<*>) {
        super.onComponentAdded(component)
        log.d{
            "COMPONENT_ADDED ${component.clazz.name}"
        }
    }

    override fun onComponentRemoved(component: ComponentHolder<*>) {
        super.onComponentRemoved(component)
        log.d{
            "COMPONENT_REMOVED ${component.clazz.name}"
        }
    }

    companion object{
        fun createHolder(bundle: Bundle = Bundle()) : ComponentHolder<*>{
            bundle.putString("test", "test")
            return ComponentHolder(DummyListener::class.java, bundle)
        }
    }
}