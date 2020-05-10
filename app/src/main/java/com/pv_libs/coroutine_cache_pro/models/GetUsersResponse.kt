package com.pv_libs.coroutine_cache_pro.models


import com.google.gson.annotations.SerializedName

data class GetUsersResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("data")
    val users: List<User>,
    @SerializedName("ad")
    val ad: Ad
)