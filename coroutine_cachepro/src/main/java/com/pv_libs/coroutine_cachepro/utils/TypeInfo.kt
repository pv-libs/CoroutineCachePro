package com.pv_libs.coroutine_cachepro.utils

import com.pv_libs.cachepro_rxjava.utils.childType
import com.pv_libs.cachepro_rxjava.utils.rawType
import com.pv_libs.coroutine_cachepro.ApiResult
import com.pv_libs.coroutine_cachepro.api_caller.CoroutineApiCaller
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.lang.reflect.Type


class ReturnTypeInfo(
    val returnType: Type,
    val responseType: Type,
    val outerLayer: Type,
    val hasApiResult: Boolean,
    val hasResponse: Boolean
) {
    fun validate(): Boolean {
        // Validating for RxApiCaller
        if (outerLayer == CoroutineApiCaller::class.java && !hasApiResult && !hasResponse) {
            // returnType -> RxApiCaller<NetworkResponse>  -- supported
            // RxApiCaller should not have ApiResult or Response as childType
            return true
        }
        // -----

        // Validating for Observable
        if (outerLayer == Flow::class.java) {

            // Validating for ApiResult under Observable
            if (hasApiResult && hasResponse) {
                // returnType -> Observable<ApiResult<Response<NetworkResponse>>> -- supported
                return true
            }
            if (hasApiResult && !hasResponse) {
                // returnType -> Observable<ApiResult<NetworkResponse>> -- not supported
                return false
            }
            // ------

            // Validating for Response under Observable
            // returnType -> Observable<Response<NetworkResponse>> -- supported
            // returnType -> Observable<NetworkResponse> -- not supported
            return hasResponse
        }

        return false
    }
}


internal fun Type.getReturnTypeInfo(): ReturnTypeInfo? {
    val outerLayer = rawType
    if (outerLayer != Flow::class.java && rawType != CoroutineApiCaller::class.java) {
        // Right now we only support Flow and CoroutineApiCaller
        return null
    }

    var hasApiResult = false
    var hasResponse = false

    var subType = childType ?: return null

    // checking if return type contains ApiResult
    if (subType.rawType == ApiResult::class.java) {
        hasApiResult = true
        // setting the child type of ApiResult as the new subType
        subType = subType.childType ?: return null
    }

    // checking if return type contains Response
    if (subType.rawType == Response::class.java) {
        hasResponse = true
        // setting the child type of Response as the new subType
        subType = subType.childType ?: return null
    }
    val responseType = subType

    return ReturnTypeInfo(
        this,
        responseType,
        outerLayer,
        hasApiResult,
        hasResponse
    )
}
