package org.btelman.control.sdk.demo

import android.app.Application

import org.btelman.logutil.kotlin.LogLevel
import org.btelman.logutil.kotlin.LogUtil
import java.io.File

/**
 * Demo Application setup
 */
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Set the log levels and tag. Tag has the same limitations as Log tag
        val pw = File(cacheDir, "ControlSDK-${System.currentTimeMillis()}").printWriter()
        LogUtil.init("ControlSDK", LogLevel.DEBUG, pw)
    }
}
