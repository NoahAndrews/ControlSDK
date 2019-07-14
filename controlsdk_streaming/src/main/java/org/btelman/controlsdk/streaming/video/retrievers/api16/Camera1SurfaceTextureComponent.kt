package org.btelman.controlsdk.streaming.video.retrievers.api16

import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.Camera
import org.btelman.controlsdk.streaming.models.CameraDeviceInfo
import org.btelman.controlsdk.streaming.models.ImageDataPacket
import org.btelman.controlsdk.streaming.models.StreamInfo
import org.btelman.controlsdk.streaming.video.retrievers.SurfaceTextureVideoRetriever


/**
 * Class that contains only the camera components for streaming to letsrobot.tv
 *
 * To make this functional, pass in cameraId and a valid SurfaceHolder to a Core.Builder instance
 *
 * This will grab the camera password automatically from config file
 *
 * Does not support USB webcams
 */
@Suppress("DEPRECATION")
class Camera1SurfaceTextureComponent : SurfaceTextureVideoRetriever(), Camera.PreviewCallback{
    private var r: Rect? = null
    private var camera : Camera? = null
    private var _widthV1 = 0
    private var _heightV1 = 0

    private var latestPackage : ImageDataPacket? = null

    override fun grabImageData(): ImageDataPacket? {
        return latestPackage
    }

    override fun onPreviewFrame(b: ByteArray?, camera: Camera?) {
        if (_widthV1 == 0 || _heightV1 == 0) {
            camera?.parameters?.let {
                val size = it.previewSize
                _widthV1 = size.width
                _heightV1 = size.height
                r = Rect(0, 0, _widthV1, _heightV1)
            }
        }
        latestPackage = ImageDataPacket(b, ImageFormat.NV21, r)
    }

    override fun releaseCamera() {
        camera?.stopPreview()
        camera?.setPreviewCallback (null)
        camera?.setPreviewTexture(null)
        camera?.release()
        camera = null
    }

    override fun setupCamera(streamInfo : StreamInfo?){ //TODO actually use resolution from here?
        //TODO use StreamInfo camera id
        camera ?: run {
            val cameraId = streamInfo?.deviceInfo?.camera?.let {
                CameraDeviceInfo.getCameraId(it)
            } ?: 0
            
            camera = Camera.open(cameraId)
            camera?.setDisplayOrientation(90)
        }
        camera?.let {
            val p = it.parameters
            p.setPreviewSize(640, 480)
            it.parameters = p
            it.setPreviewTexture(mStManager?.surfaceTexture)
            it.setPreviewCallback(this)
            it.startPreview()
        }
    }
}
