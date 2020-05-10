package com.pv_libs.coroutine_cachepro

import com.pv_libs.coroutine_cachepro.interceptors.CacheProInterceptor
import com.pv_libs.coroutine_cachepro.interceptors.CacheProNetworkInterceptor
import com.pv_libs.coroutine_cachepro.utils.NetworkUtils
import okhttp3.Interceptor

internal class CacheProImpl internal constructor(private val builder: CachePro.Builder) : CachePro {

    override fun getNetworkInterceptor(): Interceptor {
        return CacheProNetworkInterceptor(builder.forceCache)
    }

    override fun getInterceptor(): Interceptor {
        return CacheProInterceptor(getNetworkUtils(), builder.enableOffline)
    }

    private fun getNetworkUtils() = NetworkUtils(builder.context)

}