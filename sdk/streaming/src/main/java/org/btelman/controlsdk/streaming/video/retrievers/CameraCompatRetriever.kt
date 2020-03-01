package org.btelman.controlsdk.streaming.video.retrievers

import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.runBlocking
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.video.retrievers.api16.Camera1SurfaceTextureComponent
import org.btelman.controlsdk.streaming.video.retrievers.api21.Camera2Component

/**
 * Handle compatibility between camera1 and camera2 usage, since some api21 devices are
 * not compatible, which makes frame grabbing really slow. Usage of Camera1 or Camera2 classes are
 * still supported, but may not work on every device
 * ex. Samsung Galaxy S4
 */
open class CameraCompatRetriever : BaseVideoRetriever(){
    private var retriever : BaseVideoRetriever? = null

    override fun grabImageData(): ImageDataPacket? {
        return retriever?.grabImageData()
    }

    override fun enableInternal() {
        super.enableInternal()
        val cameraInfo = streamInfo!!.deviceInfo
        val cameraId = cameraInfo.getCameraId()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && validateCamera2Support(context!!, cameraId)){
            Log.d("CameraRetriever", "Using Camera2 API")
            retriever = createCamera2()
        }
        else{
            Log.d("CameraRetriever",
                "Using Camera1 API. Device API too low or LIMITED capabilities")
            retriever = createCamera1()
        }
        runBlocking {
            retriever?.enable()?.await()
        }
    }

    private fun createCamera1(): BaseVideoRetriever? {
        return Camera1SurfaceTextureComponent()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    protected open fun createCamera2(): BaseVideoRetriever? {
        return Camera2Component()
    }

    override fun listenForFrame(func: () -> Unit) {
        retriever?.listenForFrame(func)
    }

    override fun removeListenerForFrame() {
        retriever?.removeListenerForFrame()
    }

    override fun disableInternal() {
        super.disableInternal()
        runBlocking {
            retriever?.disable()?.await()
        }
        retriever = null
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun validateCamera2Support(context: Context, cameraId: Int): Boolean {
        try {
            val cm = (context.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
            val hardwareLevel = cm.getCameraCharacteristics(
                cm.cameraIdList[cameraId]
            )[CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL]
            return hardwareLevel != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
                    && hardwareLevel != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
        } catch (_: Exception) {

        }
        return false
    }
}