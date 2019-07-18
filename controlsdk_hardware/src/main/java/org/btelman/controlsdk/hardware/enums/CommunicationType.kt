package org.btelman.controlsdk.hardware.enums

import org.btelman.controlsdk.hardware.drivers.BluetoothClassicCommunication
import org.btelman.controlsdk.hardware.drivers.FelhrUsbSerialCommunication
import org.btelman.controlsdk.hardware.interfaces.CommunicationInterface

/**
 * communication types will reside in here
 */
enum class CommunicationType {
    UsbSerial,
    BluetoothClassic;

    val getInstantiatedClass : CommunicationInterface?
        get() = when(this){
            BluetoothClassic -> BluetoothClassicCommunication()
            UsbSerial -> FelhrUsbSerialCommunication()
        }
}