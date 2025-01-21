package com.example.kanicard.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

//Kiểm tra internet
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityStatus = context.getSystemService<ConnectivityManager>() ?: return false
    val activeNetwork = connectivityStatus.activeNetwork ?: return false

    val networkCapabilities =
        connectivityStatus.getNetworkCapabilities(activeNetwork) ?: return false

    return when {
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}