package org.btelman.controlsdk.utils

import android.content.Context
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexFile
import java.lang.reflect.Field

/**
 * Scan for classes with annotations. This will scan all files, so may take a while on older devices
 */
object ClassScanner {
    private var cachedDexClasses: ArrayList<Class<*>>? = null

    fun <A : Annotation> getClassesWithAnnotation(context: Context, annotation: Class<A>): ArrayList<Class<*>> {
        val clazzes = getClasses(context) //init first
        val list = ArrayList<Class<*>>()
        clazzes.forEach { clazz ->
            clazz.getAnnotation(annotation)?.let {
                list.add(clazz)
            }
        }
        return list
    }

    fun getClasses(context: Context) : ArrayList<Class<*>> {
        cachedDexClasses?.let{ return ArrayList(it) }
        val list = ArrayList<Class<*>>()
        val dexes = getDexFiles(context)
        val loader = context.classLoader
        dexes.forEach {
            val iter = it.entries()
            while (iter.hasMoreElements()) {
                val className = iter.nextElement()
                try {
                    if (!className.contains("$")) {
                        list.add(Class.forName(className, false, loader))
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        cachedDexClasses = list
        return ArrayList(list)
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