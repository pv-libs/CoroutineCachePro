package com.pv_libs.coroutine_cache_pro.models


import com.google.gson.annotations.SerializedName

data class Ad(
    @SerializedName("company")
    val company: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("text")
    val text: String
)