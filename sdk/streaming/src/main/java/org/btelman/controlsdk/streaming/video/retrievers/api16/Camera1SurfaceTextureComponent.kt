package org.btelman.controlsdk.streaming.video.retrievers.api16

import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.Camera
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
    private var supportedPreviewSizes: MutableList<Camera.Size>? = null
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
        val cameraId = streamInfo?.deviceInfo?.getCameraId() ?: 0
        val cameraWidth = streamInfo?.width ?: 640
        val cameraHeight = streamInfo?.height ?: 480
        camera ?: run {
            if(cameraId+1 > Camera.getNumberOfCameras())
                throw Exception("Attempted to open camera $cameraId. Only ${Camera.getNumberOfCameras()} cameras exist! 0 is first camera")
            camera = Camera.open(cameraId)
            camera?.setDisplayOrientation(90)
        }
        camera?.let {
            val p = it.parameters
            supportedPreviewSizes = p.supportedPreviewSizes
            var supportsSize = false
            supportedPreviewSizes?.forEach { size ->
                if(size.height == cameraHeight && size.width == cameraWidth){
                    supportsSize = true
                }
            }
            if(!supportsSize) throw java.lang.Exception("Camera size " +
                    "${cameraWidth}x$cameraHeight not supported by this camera!")
            p.setPreviewSize(cameraWidth, cameraHeight)
            p.setRecordingHint(true)
            it.parameters = p
            it.setPreviewTexture(mStManager?.surfaceTexture)
            it.setPreviewCallback(this)
            it.startPreview()
        }
    }
}
