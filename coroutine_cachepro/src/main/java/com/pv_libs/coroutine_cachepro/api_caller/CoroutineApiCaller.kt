package com.pv_libs.coroutine_cachepro.api_caller

import androidx.lifecycle.LiveData
import com.pv_libs.coroutine_cachepro.ApiResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface CoroutineApiCaller<NetworkResponse> {

    val isApiInProgressLiveData: LiveData<Boolean>

    fun getResponseFlow(callServerOnSubscribe: Boolean = true): Flow<ApiResult<Response<NetworkResponse>>>

    fun fetchFromServer()

}