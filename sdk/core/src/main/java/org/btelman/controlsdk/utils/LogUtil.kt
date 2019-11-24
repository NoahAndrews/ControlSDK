package org.btelman.controlsdk.utils

import android.util.Log

/**
 * Logging Util for the Control SDK with log levels
 */
class LogUtil(val tag : String) {

    inline fun v(logString : ()->String){
        if(logLevel == LogLevel.VERBOSE){
            v(logString(), checkShouldLog = false)
        }
    }

    inline fun d(logString : ()->String){
        if(logLevel <= LogLevel.DEBUG){
            d(logString(), checkShouldLog = false)
        }
    }

    inline fun w(logString : ()->String){
        if(logLevel <= LogLevel.WARNING){
            w(logString(), checkShouldLog = false)
        }
    }

    inline fun e(logString : ()->String){
        if(logLevel <= LogLevel.ERROR){
            e(logString(), checkShouldLog = false)
        }
    }

    inline fun e(e : Exception, logString : ()->String){
        if(logLevel <= LogLevel.ERROR){
            e(logString(), e, checkShouldLog = false)
        }
    }

    fun v(message : String, checkShouldLog : Boolean = true){
        if(!checkShouldLog || logLevel == LogLevel.VERBOSE)
            Log.v(globalLogTag, buildMessage(message))
    }

    fun d(message : String, checkShouldLog : Boolean = true){
        if(!checkShouldLog || logLevel <= LogLevel.DEBUG)
            Log.d(globalLogTag, buildMessage(message))
    }

    fun w(message : String, checkShouldLog : Boolean = true){
        if(!checkShouldLog || logLevel <= LogLevel.WARNING)
            Log.w(globalLogTag, buildMessage(message))
    }

    fun e(message : String, checkShouldLog : Boolean = true){
        if(!checkShouldLog || logLevel <= LogLevel.ERROR)
            Log.e(globalLogTag, buildMessage(message))
    }

    fun e(message : String, exception: Exception, checkShouldLog : Boolean = true){
        if(!checkShouldLog || logLevel <= LogLevel.ERROR)
            Log.e(globalLogTag, buildMessage(message), exception)
    }

    private fun buildMessage(message: String): String {
        return "$tag : $message"
    }

    companion object{
        var logLevel : LogLevel = LogLevel.WARNING
        var globalLogTag = "Controller"
    }
}

enum class LogLevel{
    VERBOSE,
    DEBUG,
    WARNING,
    ERROR,
    NONE
}