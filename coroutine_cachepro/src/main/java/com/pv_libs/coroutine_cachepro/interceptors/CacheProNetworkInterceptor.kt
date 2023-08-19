package com.pv_libs.coroutine_cachepro.interceptors

import com.pv_libs.coroutine_cachepro.BuildConfig
import com.pv_libs.coroutine_cachepro.annotations.ApiCache
import com.pv_libs.coroutine_cachepro.annotations.ApiNoCache
import com.pv_libs.coroutine_cachepro.utils.CACHE_CONTROL
import com.pv_libs.coroutine_cachepro.utils.GET
import com.pv_libs.coroutine_cachepro.utils.hasAnnotation
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.Response


/**
 * This [Interceptor] modifies network [Response]. To force [Cache] to save the [Response],
 * if API method is GET and is not annotated with [ApiNoCache]
 */
internal class CacheProNetworkInterceptor(
    private val forceCache: Boolean
) : Interceptor {

    /**
     * intercepting the [Response] and modifies network response to force [Cache] to save the [Response],
     * if API method is GET and is not annotated with [ApiNoCache]
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.method.equals(GET, true)) return response

        val isAnnotatedAsApiNoCache = request.hasAnnotation(ApiNoCache::class.java)
        val isAnnotatedAsApiCache = request.hasAnnotation(ApiCache::class.java)


        val requestBuilder = request.newBuilder()
        if (isAnnotatedAsApiCache || (forceCache && !isAnnotatedAsApiNoCache)) {
            if (BuildConfig.DEBUG) {
                // overriding the cache control send by the server
                requestBuilder.removeHeader(CACHE_CONTROL)
            }
            requestBuilder.removeHeader(CACHE_CONTROL)
            requestBuilder.addHeader(CACHE_CONTROL, "private, max-age=0")
        }

        return response
    }


}