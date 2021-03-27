package com.pv_libs.coroutine_cache_pro.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.pv_libs.coroutine_cachepro.CachePro
import com.pv_libs.coroutine_cachepro.adapters.CoroutineCacheProCallAdapter
import com.pv_libs.coroutine_cachepro.attachCachePro
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object Retrofit {

    private const val CACHE_SIZE = 100 * 1024 * 1020 // 50MB

    fun getCoroutineApiService(context: Context): RxApiService {
        return provideRetrofit(context.applicationContext)
            .create(RxApiService::class.java)
    }

    private fun provideRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://reqres.in")
            .client(provideOkHttpClient(context))
            .addCallAdapterFactory(CoroutineCacheProCallAdapter.Factory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {

        val cache = provideCache(context)

        val okHttpClientBuilder = OkHttpClient.Builder()

        okHttpClientBuilder.cache(cache)

        val cachePro = CachePro.Builder(context).build()
        okHttpClientBuilder.attachCachePro(cachePro)

        // For observing api calls with GUI
        okHttpClientBuilder.addInterceptor(ChuckerInterceptor.Builder(context).build())
        okHttpClientBuilder.addNetworkInterceptor(ChuckerInterceptor.Builder(context).build())


        return okHttpClientBuilder.build()
    }

    private fun provideCache(context: Context): Cache {
        val cacheFolder = File(context.cacheDir, "apiCache")
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs()
        }
        return Cache(cacheFolder, CACHE_SIZE.toLong())
    }

}