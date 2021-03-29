package com.pv_libs.coroutine_cachepro.annotations

@Retention
@Target(AnnotationTarget.FUNCTION)
internal annotation class ForceCacheCall

@Retention
@Target(AnnotationTarget.FUNCTION)
internal annotation class ForceNetworkCall