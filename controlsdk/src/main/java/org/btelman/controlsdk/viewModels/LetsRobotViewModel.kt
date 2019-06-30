package org.btelman.controlsdk.viewModels

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import org.btelman.controlsdk.enums.Operation
import org.btelman.controlsdk.interfaces.IControlSdkApi
import org.btelman.controlsdk.services.ControlSDKServiceApi

/**
 * ViewModel to handle LetsRobot controller logic,
 * and automatically destroy listeners when activity is killed
 */
class LetsRobotViewModel : ViewModel(){

    var api : IControlSdkApi? = null
        private set

    private var serviceStatusObserver : Observer<Operation>? = null
    private var serviceConnectionObserver : Observer<Operation>? = null

    /**
     * Calls api.getServiceStateObserver().observe(activity, observer) in ILetsRobotControl
     * @see IControlSdkApi.getServiceStateObserver(activity, observer)
     */
    fun setStatusObserver(activity: FragmentActivity, observer : (Operation) -> Unit){
        serviceStatusObserver = Observer{
            observer(it)
        }
        api?.getServiceStateObserver()?.observe(activity, serviceStatusObserver!!)
    }

    /**
     * Calls api.getServiceBoundObserver().observe(activity, observer) in IControlSdkApi
     * @see IControlSdkApi.getServiceBoundObserver(activity, observer)
     */
    fun setServiceBoundListener(activity: FragmentActivity, observer : (Operation) -> Unit){
        serviceConnectionObserver = Observer{
            observer(it)
        }
        api?.getServiceBoundObserver()?.observe(activity, serviceConnectionObserver!!)
    }

    override fun onCleared() {
        super.onCleared()
        api?.disconnectFromService()
    }

    companion object {
        fun getObject(activity: FragmentActivity) : LetsRobotViewModel {
            return ViewModelProviders.of(activity).get(LetsRobotViewModel::class.java).also {
                if (it.api == null) {
                    it.api = ControlSDKServiceApi.getNewInstance(activity).also { api ->
                        api.connectToService()
                    }
                }
            }
        }
    }
}