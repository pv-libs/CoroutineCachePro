package com.pv_libs.coroutine_cachepro.utils

import okhttp3.Request
import retrofit2.Invocation
import retrofit2.Response

fun Response<*>.isFromCache() =
    raw().networkResponse() == null || raw().networkResponse()?.code() == 304


/**
 * this Extension-Function is for checking whether the API request has been annotated with a specific [Annotation]
 * @param classType should be the [Class] of the [Annotation] that needs to be checked
 * @return boolean
 */
internal fun <T : Annotation> Request.hasAnnotation(classType: Class<T>) =
    tag(Invocation::class.java)?.method()?.getAnnotation(classType) != null

