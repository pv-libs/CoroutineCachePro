package com.pv_libs.cachepro_rxjava.utils

internal fun Array<Annotation>.add(t: Annotation) = toMutableList()
    .apply {
        add(t)
    }.toTypedArray()
