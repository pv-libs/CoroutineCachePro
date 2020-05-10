package com.pv_libs.coroutine_cachepro.annotations

internal class Annotations {
    private val forceNetworkCallAnnotation: ForceNetworkCall by lazy {
        Annotations::class.java.getDeclaredMethod("getForceNetworkCall")
            .getAnnotation(ForceNetworkCall::class.java)
    }

    private val forceCacheCallAnnotation: ForceCacheCall by lazy {
        Annotations::class.java.getDeclaredMethod("getForceCacheCall")
            .getAnnotation(ForceCacheCall::class.java)
    }

    @ForceNetworkCall
    fun getForceNetworkCall() = forceNetworkCallAnnotation

    @ForceCacheCall
    fun getForceCacheCall() = forceCacheCallAnnotation
}



