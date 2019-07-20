package org.btelman.controlsdk.utils

import android.os.Bundle

/**
 * Util for bundles
 */
fun <T> Class<T>.toBundle(key : String) : Bundle {
    return intoBundle(key, Bundle())
}

fun <T> Class<T>.intoBundle(key : String, bundle: Bundle) : Bundle {
    bundle.putSerializable(key, this::class.java)
    return bundle
}

object BundleUtil{
    fun <T> checkForAndInitClass(clazz : Class<*>?, base : Class<T>) : T?{
        return if(checkIfClassMatches(clazz, base))
            clazz!!.newInstance() as T
        else
            null
    }

    fun <T> checkIfClassMatches(clazz: Class<*>?, base : Class<T>) : Boolean{
        return clazz?.let {
            if(base.isAssignableFrom(clazz)){
                return true
            } else false
        } ?: false
    }

    fun getClassFromBundle(bundle: Bundle, key : String) : Class<*>?{
        return bundle.getSerializable(key) as? Class<*>
    }
}