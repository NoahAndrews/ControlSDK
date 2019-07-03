package org.btelman.controlsdk.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.btelman.controlsdk.services.ControlSDKService

/**
 * Created by Brendon on 7/2/2019.
 */
object NotificationUtil {
    fun tryCreateNotificationChannel(context: Context, name : String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            //create a notification channel
            val mChannel = NotificationChannel(ControlSDKService.CONTROL_SERVICE, name, importance)
            mChannel.description = description
            mChannel.enableLights(false)
            mChannel.setSound(null, null)
            mChannel.setShowBadge(false)
            mChannel.enableVibration(false)
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            try {
                mNotificationManager.createNotificationChannel(mChannel)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }
}