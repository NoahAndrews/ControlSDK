package org.btelman.controlsdk.interfaces

/**
 * Base methods that any controller requires
 */
interface IController : IControlSDKElement{

    fun onControlAPI(controlSdkApi: ControlSDKMessenger){

    }

    /**
     * Set an event listener for this component
     */
    fun setEventListener(listener : ComponentEventListener?){}
}