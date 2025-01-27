package com.nmheir.kanicard.extensions

import android.os.Build
import com.google.android.material.color.DynamicColors
import com.nmheir.kanicard.utils.DeviceUtil

val DeviceUtil.isDynamicColorAvailable by lazy {
    DynamicColors.isDynamicColorAvailable() || (DeviceUtil.isSamsung && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
}