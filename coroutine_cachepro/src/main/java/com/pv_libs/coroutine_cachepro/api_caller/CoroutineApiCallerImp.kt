package com.pv_libs.coroutine_cachepro.api_caller

import android.util.Log
import com.pv_libs.coroutine_cachepro.ApiResult
import com.pv_libs.coroutine_cachepro.utils.isFromCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response

internal class CoroutineApiCallerImp<NetworkResponse>(
    private val builder: Builder<NetworkResponse>
) : CoroutineApiCaller<NetworkResponse> {
    private val TAG = "CoroutineApiCallerImp"
    private val isApiInProgressMutableStateFlow = MutableStateFlow(false)
    override val isApiInProgressSateFlow: StateFlow<Boolean> = isApiInProgressMutableStateFlow

    private val networkCallsMutableFlow =
        MutableSharedFlow<Flow<Response<NetworkResponse>>>()

    private val cacheCallFlow = flow {
        try {
            log("emit getNewCacheCall")
            emit(builder.getResponseFromCache())
        } catch (e: Exception) {
            // error in cache call is ignored
        }
    }

    private val responseFlow = merge(cacheCallFlow, networkCallsMutableFlow.flattenConcat())
        .filter {
            log("responseFlow filter")
            log("isLoadedAtLeastOnce - $isLoadedAtLeastOnce")
            log("isFromCache - ${it.isFromCache()}")
            if (isLoadedAtLeastOnce && it.isFromCache()) {
                false
            } else {
                isLoadedAtLeastOnce = true
                true
            }
        }
        .map {
            log("responseFlow - ApiResult.Success ")
            ApiResult.Success(it)
        }
        .catch {
            log("responseFlow - ApiResult.Error")
            ApiResult.Error(it as Exception)
        }
        .flowOn(Dispatchers.IO)

    private var isLoadedAtLeastOnce = false

    override fun getResponseFlow(callServerOnCollect: Boolean): Flow<ApiResult<Response<NetworkResponse>>> {
        if (callServerOnCollect) {
            fetchFromServer()
        }
        return responseFlow
    }

    override fun fetchFromServer() {
        log("fetchFromServer")
        try {
            runBlocking {
                // runBlocking is only used because below function never suspends
                networkCallsMutableFlow.emit(flow {
                    log("emit NewServerCall")
                    emit(builder.getResponseFromServer())
                }.onStart {
                    log("isApiInProgressMutableStateFlow.value = true")
                    isApiInProgressMutableStateFlow.value = true
                }.onCompletion {
                    log("isApiInProgressMutableStateFlow.value = false")
                    isApiInProgressMutableStateFlow.value = false
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun log(message: String) = Log.d(TAG, message)

    class Builder<NetworkResponse>(
        private val originalCall: Call<NetworkResponse>,
        private val cacheCallAdapter: CallAdapter<NetworkResponse, Call<NetworkResponse>>,
        private val serverCallAdapter: CallAdapter<NetworkResponse, Call<NetworkResponse>>
    ) {
        fun build(): CoroutineApiCaller<NetworkResponse> =
            CoroutineApiCallerImp(this)

        fun getResponseFromCache(): Response<NetworkResponse> =
            cacheCallAdapter.adapt(originalCall.clone()).execute()

        fun getResponseFromServer(): Response<NetworkResponse> =
            serverCallAdapter.adapt(originalCall.clone()).execute()

    }

}