package com.pv_libs.coroutine_cachepro.api_caller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pv_libs.coroutine_cachepro.ApiResult
import com.pv_libs.coroutine_cachepro.utils.isFromCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response

internal class CoroutineApiCallerImp<NetworkResponse>(
    private val builder: Builder<NetworkResponse>
) : CoroutineApiCaller<NetworkResponse> {

    private val isApiInProgressMutableLiveData = MutableLiveData<Boolean>()
    override val isApiInProgressLiveData: LiveData<Boolean> = isApiInProgressMutableLiveData

    private val networkCallsBroadcastChannel =
        ConflatedBroadcastChannel<Flow<Response<NetworkResponse>>>()

    private val networkCallsFlow = networkCallsBroadcastChannel
        .asFlow()
        .flattenConcat()

    private val cacheCallFlow = flow {
        try {
            emit(builder.getNewCacheCall())
        } catch (e: Exception) {
            // error in cache call is ignored
        }
    }

    private val responseFlow = merge(cacheCallFlow, networkCallsFlow)
        .filter {
            if (isLoadedAtLeastOnce && it.isFromCache()) {
                false
            } else {
                isLoadedAtLeastOnce = true
                true
            }
        }
        .map {
            ApiResult.Success(it)
        }
        .catch {
            ApiResult.Error(it as Exception)
        }
        .flowOn(Dispatchers.IO)

    private var isLoadedAtLeastOnce = false

    override fun getResponseFlow(callServerOnSubscribe: Boolean): Flow<ApiResult<Response<NetworkResponse>>> {
        if (callServerOnSubscribe) {
            fetchFromServer()
        }
        return responseFlow
    }

    override fun fetchFromServer() {
        try {
            runBlocking {
                // runBlocking is only used because below function never suspends
                networkCallsBroadcastChannel.send(flow {
                    emit(builder.getNewServerCall())
                }.onStart {
                    isApiInProgressMutableLiveData.postValue(true)
                }.onCompletion {
                    isApiInProgressMutableLiveData.postValue(false)
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class Builder<NetworkResponse>(
        private val originalCall: Call<NetworkResponse>,
        private val cacheCallAdapter: CallAdapter<NetworkResponse, Call<NetworkResponse>>,
        private val serverCallAdapter: CallAdapter<NetworkResponse, Call<NetworkResponse>>
    ) {
        fun build(): CoroutineApiCaller<NetworkResponse> =
            CoroutineApiCallerImp(this)

        fun getNewCacheCall() =
            cacheCallAdapter.adapt(originalCall.clone()).execute()

        fun getNewServerCall() =
            serverCallAdapter.adapt(originalCall.clone()).execute()

    }

}