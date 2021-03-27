package com.pv_libs.coroutine_cachepro.api_caller

import com.pv_libs.coroutine_cachepro.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

interface CoroutineApiCaller<NetworkResponse> {

    val isApiInProgressSateFlow: StateFlow<Boolean>

    fun getResponseFlow(callServerOnCollect: Boolean = true): Flow<ApiResult<Response<NetworkResponse>>>

    fun fetchFromServer()

}