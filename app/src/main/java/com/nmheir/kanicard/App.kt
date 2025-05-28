package com.nmheir.kanicard

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.nmheir.kanicard.constants.MaxImageCacheSizeKey
import com.nmheir.kanicard.utils.dataStore
import com.nmheir.kanicard.utils.get
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        WebViewPreloader.warmUp(applicationContext)

        registerActivityLifecycleCallbacks(AppLifecycleTracker())
    }

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

class AppLifecycleTracker : Application.ActivityLifecycleCallbacks {
    private val activityStack = mutableListOf<String>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.add(activity::class.java.simpleName)
        Timber.tag("ActivityStack").d("Created: ${activity::class.java.simpleName}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity::class.java.simpleName)
        Timber.tag("ActivityStack").d("Destroyed: ${activity::class.java.simpleName}")
    }

    fun printActivityStack() {
        Timber.tag("ActivityStack").d("Current stack: $activityStack")
    }

    // Các phương thức còn lại có thể để trống
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}

object WebViewPreloader {
    private var isInitialized = false

    fun warmUp(context: Context) {
        if (isInitialized) return
        isInitialized = true

        Handler(Looper.getMainLooper()).post {
            try {
                WebView(context).apply {
                    settings.javaScriptEnabled = false
                    loadDataWithBaseURL(null, "<html></html>", "text/html", "UTF-8", null)
                    destroy() // Không cần giữ lại WebView
                }
            } catch (e: Exception) {
                Timber.tag("WebViewPreloader").w("Warm-up failed: ${e.message}")
            }
        }
    }
}
