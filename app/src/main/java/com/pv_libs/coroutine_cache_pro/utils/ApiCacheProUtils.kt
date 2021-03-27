package com.pv_libs.coroutine_cache_pro.utils

import com.pv_libs.coroutine_cachepro.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import retrofit2.Response

fun <NetworkResponse> Flow<ApiResult<Response<NetworkResponse>>>.unWrapApiResult(
    onError: ((Exception) -> Unit)? = null
): Flow<Response<NetworkResponse>> =
    transformLatest { apiResult ->
        when (apiResult) {
            is ApiResult.Success -> {
                emit(apiResult.data)
            }
            is ApiResult.Error -> {
                onError?.invoke(apiResult.exception)
            }
        }
    }


fun <NetworkResponse> Flow<ApiResult<Response<NetworkResponse>>>.unWrapResponse(
    onError: ((Exception) -> Unit)? = null
): Flow<NetworkResponse> =
    unWrapApiResult(onError).transformLatest { response ->
        val responseBody = response.body()
        if (!response.isSuccessful || responseBody == null) {
            onError?.invoke(Exception(response.message()))
        } else {
            emit(responseBody)
        }
    }



