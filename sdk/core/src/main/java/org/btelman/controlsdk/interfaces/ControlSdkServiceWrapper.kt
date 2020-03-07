package org.btelman.controlsdk.interfaces

import androidx.lifecycle.LiveData
import org.btelman.controlsdk.enums.Operation

/**
 * Interface for communicating with the robot service
 */
interface ControlSdkServiceWrapper : ControlSDKMessenger{

    /**
     * Subscribe to status events.
     * @return androidx.lifecycle.LiveData<Boolean>
     */
    fun getServiceStateObserver() : LiveData<Operation>

    /**
     * Subscribe to service connection events.
     * @return androidx.lifecycle.LiveData<Boolean>
     */
    fun getServiceBoundObserver() : LiveData<Operation>

    /**
     * Disconnect from service. Calling this does not terminate the service.
     */
    fun disconnectFromService()

    /**
     * Connect to the service
     */
    fun connectToService()
}