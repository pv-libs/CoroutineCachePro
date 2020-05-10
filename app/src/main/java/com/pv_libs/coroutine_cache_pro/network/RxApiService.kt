package com.pv_libs.coroutine_cache_pro.network

import com.pv_libs.coroutine_cache_pro.models.GetUsersResponse
import com.pv_libs.coroutine_cachepro.annotations.ApiNoCache
import com.pv_libs.coroutine_cachepro.api_caller.CoroutineApiCaller
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET

interface RxApiService {

    @ApiNoCache                                                          // use @ApiNoCache to disable the cache
    @GET("/api/users")
    suspend fun getUsersSingle(): Response<GetUsersResponse>  //  sample api for disabling the cache

    @GET("/api/users")
    fun getUsersObservable(): Flow<Response<GetUsersResponse>>     // sample api with Observable

    @GET("/api/users")
    fun getUsersApiCaller(): CoroutineApiCaller<GetUsersResponse>               // sample api with RxApiCaller

}