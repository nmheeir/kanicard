package com.example.kanicard

import android.app.Application
import android.os.Build
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.example.kanicard.constants.MaxImageCacheSizeKey
import com.example.kanicard.utils.dataStore
import com.example.kanicard.utils.get
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext) = ImageLoader.Builder(context)
        .crossfade(true)
        .allowHardware(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        .diskCache(
            DiskCache.Builder()
                .directory(cacheDir.resolve("coil"))
                .maxSizeBytes((dataStore[MaxImageCacheSizeKey] ?: 512) * 1024 * 1024L)
                .build()
        )
        .apply {
            if (BuildConfig.DEBUG) {
                this.logger(DebugLogger())
            }
        }
        .build()
}