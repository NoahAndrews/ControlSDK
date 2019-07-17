package org.btelman.controlsdk

import android.content.Context
import android.os.Bundle
import org.btelman.controlsdk.enums.ComponentType
import org.btelman.controlsdk.models.Component
import org.btelman.controlsdk.models.ComponentHolder

/**
 * Created by Brendon on 7/1/2019.
 */
class DummyComponent : Component() {

    var bundle : Bundle? = null

    override fun onInitializeComponent(applicationContext: Context, bundle: Bundle?) {
        super.onInitializeComponent(applicationContext, bundle)
        this.bundle = bundle
    }

    override fun enableInternal() {

    }

    override fun disableInternal() {

    }

    override fun getType(): ComponentType {
        return ComponentType.CUSTOM
    }

    companion object{
        data class Builder(val arg1 : String, val arg2: Int){
            fun build() : ComponentHolder<DummyComponent> {
                val bundle = Bundle()
                bundle.putString(ARG1_KEY, arg1)
                bundle.putInt(ARG2_KEY, arg2)
                return ComponentHolder(DummyComponent::class.java, bundle)
            }
        }

        const val ARG1_KEY = "arg1"
        const val ARG2_KEY = "arg2"
    }
}