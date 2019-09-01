package org.btelman.controlsdk.utils

import android.os.Bundle

/**
 * Util for bundles
 */
object BundleUtil{
    fun toBundle(key : String, clazz : Class<*>) : Bundle {
        return intoBundle(key, clazz, Bundle())
    }

    fun intoBundle(key : String, clazz : Class<*>, bundle: Bundle) : Bundle {
        bundle.putSerializable(key, clazz)
        return bundle
    }

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