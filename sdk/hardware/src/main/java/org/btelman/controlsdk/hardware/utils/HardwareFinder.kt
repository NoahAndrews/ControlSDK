package org.btelman.controlsdk.hardware.utils

import android.content.Context
import org.btelman.controlsdk.hardware.interfaces.DriverComponent
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import org.btelman.controlsdk.utils.ClassScanner

/**
 * Find all classes that use the TranslatorAnnotation
 */
object HardwareFinder {
    fun getTranslationClasses(context: Context) : ArrayList<Class<*>>{
        return ClassScanner.getClassesWithAnnotation(context, TranslatorComponent::class.java)
    }

    fun getDriverClasses(context: Context) : ArrayList<Class<*>>{
        return ClassScanner.getClassesWithAnnotation(context, DriverComponent::class.java)
    }
}