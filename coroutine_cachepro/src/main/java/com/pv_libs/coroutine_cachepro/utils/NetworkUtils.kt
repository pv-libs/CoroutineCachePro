package com.pv_libs.coroutine_cachepro.utils

import android.content.Context
import android.net.ConnectivityManager

internal class NetworkUtils(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isNetworkConnected: Boolean
        get() {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }

}