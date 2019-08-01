package org.btelman.controlsdk.hardware.utils

import android.content.Context
import android.util.Log
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexFile
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import java.lang.reflect.Field

/**
 * Find all classes that use the TranslatorAnnotation
 */
object TranslationClassFinder {
    private val classes = ArrayList<Class<*>>()

    fun getClasses(context: Context){
        val dexes = getDexFiles(context)
        dexes.forEach {
            val iter = it.entries()
            while (iter.hasMoreElements()) {
                val className = iter.nextElement()

                try {
                    if (!className.contains("$")) {
                        classes.add(Class.forName(className, false, null))
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        classes.forEach {clazz ->
            clazz.getAnnotation(TranslatorComponent::class.java)?.let {
                Log.d("SCAN", clazz.name)
            }
        }
    }

    internal fun getDexFiles(context: Context): List<DexFile> {
        // Here we do some reflection to access the dex files from the class loader. These implementation details vary by platform version,
        // so we have to be a little careful, but not a huge deal since this is just for testing. It should work on 21+.
        // The source for reference is at:
        // https://android.googlesource.com/platform/libcore/+/oreo-release/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
        val classLoader = context.classLoader as BaseDexClassLoader

        val pathListField = field("dalvik.system.BaseDexClassLoader", "pathList")
        val pathList = pathListField.get(classLoader) // Type is DexPathList

        val dexElementsField = field("dalvik.system.DexPathList", "dexElements")
        @Suppress("UNCHECKED_CAST")
        val dexElements = dexElementsField.get(pathList) as Array<Any> // Type is Array<DexPathList.Element>

        val dexFileField = field("dalvik.system.DexPathList\$Element", "dexFile")
        return dexElements.map {
            dexFileField.get(it) as DexFile
        }
    }

    private fun field(className: String, fieldName: String): Field {
        val clazz = Class.forName(className)
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        return field
    }
}