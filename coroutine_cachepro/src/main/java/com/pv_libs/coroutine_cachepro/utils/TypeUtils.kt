package com.pv_libs.cachepro_rxjava.utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


internal val Type.rawType: Type
    get() {
        return if (this is ParameterizedType) {
            rawType
        } else {
            this
        }
    }

internal val Type.childType: Type?
    get() {
        return if (this is ParameterizedType) {
            actualTypeArguments.getOrNull(0)
        } else null
    }

internal val Type.childRawType: Type?
    get() {
        return childType?.rawType
    }

internal fun Type.wrapWith(parentType: Type): Type {
    return object : ParameterizedType {
        override fun getRawType(): Type {
            return parentType
        }

        override fun getOwnerType(): Type? {
            return null
        }

        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(this@wrapWith)
        }
    }
}
