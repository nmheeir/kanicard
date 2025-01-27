package com.nmheir.kanicard.utils

import android.annotation.SuppressLint
import android.os.Build
import timber.log.Timber

object DeviceUtil {

    val isSamsung: Boolean by lazy {
        Build.MANUFACTURER.equals("samsung", ignoreCase = true)
    }

    @SuppressLint("PrivateApi")
    private fun getSystemProperty(key: String?): String? {
        return try {
            Class.forName("android.os.SystemProperties")
                .getDeclaredMethod("get", String::class.java)
                .invoke(null, key) as String
        } catch (e: Exception) {
            Timber.w(e, "Unable to use SystemProperties.get()")
            null
        }
    }
}