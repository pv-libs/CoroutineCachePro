package com.pv_libs.coroutine_cachepro.adapters

import android.util.Log
import com.pv_libs.cachepro_rxjava.utils.add
import com.pv_libs.cachepro_rxjava.utils.wrapWith
import com.pv_libs.coroutine_cachepro.ApiResult
import com.pv_libs.coroutine_cachepro.annotations.Annotations
import com.pv_libs.coroutine_cachepro.annotations.ApiNoCache
import com.pv_libs.coroutine_cachepro.api_caller.CoroutineApiCaller
import com.pv_libs.coroutine_cachepro.api_caller.CoroutineApiCallerImp
import com.pv_libs.coroutine_cachepro.utils.ReturnTypeInfo
import com.pv_libs.coroutine_cachepro.utils.getReturnTypeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class CoroutineCacheProCallAdapter<NetworkResponse>(
    private val returnTypeInfo: ReturnTypeInfo,
    private val cacheCallAdapter: CallAdapter<NetworkResponse, Call<NetworkResponse>>,
    private val serverCallAdapter: CallAdapter<NetworkResponse, Call<NetworkResponse>>
) : CallAdapter<NetworkResponse, Any> {


    override fun adapt(call: Call<NetworkResponse>): Any {
        val apiCaller =
            CoroutineApiCallerImp.Builder(call, cacheCallAdapter, serverCallAdapter).build()
        log(returnTypeInfo.toString())
        if (returnTypeInfo.outerLayer == CoroutineApiCaller::class.java) {
            // returnType is RxApiCaller<NetworkResponse>
            return apiCaller
        }
        if (returnTypeInfo.outerLayer == Flow::class.java) {

            if (returnTypeInfo.hasApiResult && returnTypeInfo.hasResponse) {
                // returnType is Observable<ApiResult<Response<NetworkResponse>>>
                return apiCaller.getResponseFlow()
            }

            if (returnTypeInfo.hasResponse) {
                // returnType is Observable<Response<NetworkResponse>>
                return apiCaller.getResponseFlow()
                    .map {
                        when (it) {
                            is ApiResult.Success -> it.data
                            is ApiResult.Error -> throw it.exception
                        }
                    }
            }
        }
        throw IllegalAccessException("unSupported returnType - ${returnTypeInfo.returnType}")
    }

    override fun responseType(): Type {
        return returnTypeInfo.responseType
    }

    class Factory : CallAdapter.Factory() {
        @Suppress("UNCHECKED_CAST")
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *>? {

            if (annotations.any { it is ApiNoCache }) {
                return null
            }
            val returnTypeInfo = returnType.getReturnTypeInfo() ?: return null
            log(returnTypeInfo.toString())
            if (!returnTypeInfo.validate()) {
                return null
            }

            val singleResponseType = returnTypeInfo.responseType.wrapWith(Call::class.java)

            val cacheCallAdapter = retrofit.nextCallAdapter(
                this,
                singleResponseType,
                annotations.add(ANNOTATIONS.getForceCacheCall())
            ) as CallAdapter<Any?, Call<Any?>>
            val serverCallAdapter = retrofit.nextCallAdapter(
                this,
                singleResponseType,
                annotations.add(ANNOTATIONS.getForceNetworkCall())
            ) as CallAdapter<Any?, Call<Any?>>

            return CoroutineCacheProCallAdapter(
                returnTypeInfo,
                cacheCallAdapter, serverCallAdapter
            )
        }
    }
    companion object {
        private const val TAG = "CallAdapter"
        private fun log(message: String) = Log.d(TAG, message)
        private val ANNOTATIONS = Annotations()
    }
}