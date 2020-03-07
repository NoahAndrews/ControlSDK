package org.btelman.controlsdk.hardware.drivers.libs.usb

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import com.felhr.deviceids.CH34xIds
import com.felhr.deviceids.CP210xIds
import com.felhr.deviceids.FTDISioIds
import com.felhr.deviceids.PL2303Ids
import com.felhr.usbserial.*

/**
 * Created by Brendon on 3/6/2020.
 */
object UsbSerialDeviceUtil{
    fun isSupported(device: UsbDevice): Boolean {
        val vid = device.vendorId
        val pid = device.productId
        return when {
            custom.contains(Pair(vid, pid)) -> true
            FTDISioIds.isDeviceSupported(device) -> true
            CP210xIds.isDeviceSupported(
                vid,
                pid
            ) -> true
            PL2303Ids.isDeviceSupported(
                vid,
                pid
            ) -> true
            CH34xIds.isDeviceSupported(
                vid,
                pid
            ) -> true
            UsbSerialDevice.isCdcDevice(device) -> true
            else -> false
        }
    }

    private val custom = ArrayList<Pair<Int, Int>>().also {
        it.add(Pair(9025, 67)) //DFRobot Romeo BLE (Arduinooooooo)
    }

    fun createUsbSerialDevice(
        device: UsbDevice,
        connection: UsbDeviceConnection?
    ): UsbSerialDevice? {
        return createUsbSerialDevice(device, connection, -1)
    }

    fun createUsbSerialDevice(
        device: UsbDevice,
        connection: UsbDeviceConnection?,
        iface: Int
    ): UsbSerialDevice? { /*
		 * It checks given vid and pid and will return a custom driver or a CDC serial driver.
		 * When CDC is returned open() method is even more important, its response will inform about if it can be really
		 * opened as a serial device with a generic CDC serial driver
		 */
        val vid = device.vendorId
        val pid = device.productId
        return when {
            FTDISioIds.isDeviceSupported(device) || custom.contains(Pair(vid, pid)) -> FTDISerialDevice(
                device,
                connection,
                iface
            )
            CP210xIds.isDeviceSupported(vid, pid) -> CP2102SerialDevice(
                device,
                connection,
                iface
            )
            PL2303Ids.isDeviceSupported(vid, pid) -> PL2303SerialDevice(
                device,
                connection,
                iface
            )
            CH34xIds.isDeviceSupported(vid, pid) -> CH34xSerialDevice(
                device,
                connection,
                iface
            )
            UsbSerialDevice.isCdcDevice(device) -> CDCSerialDevice(
                device,
                connection,
                iface
            )
            else -> null
        }
    }
}