package com.pv_libs.coroutine_cachepro

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient

interface CachePro {

    fun getNetworkInterceptor(): Interceptor

    fun getInterceptor(): Interceptor

    class Builder(internal val context: Context) {
        internal var enableOffline = true
        internal var forceCache = true

        fun setForceCache(forceCache: Boolean): Builder {
            this.forceCache = forceCache
            return this
        }

        fun setEnableOffline(enableOffline: Boolean): Builder {
            this.enableOffline = enableOffline
            return this
        }

        fun build(): CachePro = CacheProImpl(this)
    }

}

fun OkHttpClient.Builder.attachCachePro(cachePro: CachePro) {
    addInterceptor(cachePro.getInterceptor())
    addNetworkInterceptor(cachePro.getNetworkInterceptor())
}