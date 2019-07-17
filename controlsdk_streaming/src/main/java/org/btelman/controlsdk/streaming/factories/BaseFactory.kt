package org.btelman.controlsdk.streaming.factories

import android.os.Bundle

/**
 * Created by Brendon on 7/14/2019.
 */
object BaseFactory {
    fun <T> checkForAndInitClass(clazz : Class<*>?, base : Class<T>) : T?{
        clazz?.let {
            (it as? Class<*>)?.let {clazz ->
                if(base.isAssignableFrom(clazz)){
                    return clazz.newInstance() as T
                }
            }
        }
        return null
    }

    fun getClassFromBundle(bundle: Bundle, key : String) : Class<*>?{
        return bundle.getSerializable(key) as? Class<*>
    }

    fun <T> putClassInBundle(bundle: Bundle, id: String, clazz: Class<T>) {
        bundle.putSerializable(id, clazz)
    }
}