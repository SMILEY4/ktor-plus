package io.github.smiley4.ktorplus.core

import kotlin.reflect.KType

interface ParameterEncoder<T> {
    fun canHandle(type: KType): Boolean
    fun encode(value: T?): String?

    @Suppress("UNCHECKED_CAST")
    fun encodeUnsafe(value: Any?) = encode(value as T?)
}
