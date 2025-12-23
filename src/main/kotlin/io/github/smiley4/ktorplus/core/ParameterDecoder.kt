package io.github.smiley4.ktorplus.core

import kotlinx.serialization.json.Json
import kotlin.reflect.KType

interface ParameterDecoder<T> {
    fun canHandle(type: KType): Boolean
    fun decode(value: String?, type: KType, json: Json): T?
}
