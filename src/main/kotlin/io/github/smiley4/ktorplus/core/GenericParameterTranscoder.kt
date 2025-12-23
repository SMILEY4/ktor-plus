package io.github.smiley4.ktorplus.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.jvm.java
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Encodes a given value of any time from/to string.
 */
class GenericParameterTranscoder : ParameterEncoder<Any>, ParameterDecoder<Any> {

    override fun canHandle(type: KType) = true

    override fun encode(value: Any?): String? {
        if (value == null) return null
        return value.toString()
    }

    override fun decode(value: String?, type: KType, json: Json): Any? {
        if (value == null) return null

        return when (val classifier = type.classifier) {
            String::class -> value
            Int::class -> value.toInt()
            Long::class -> value.toLong()
            Float::class -> value.toFloat()
            Double::class -> value.toDouble()
            Boolean::class -> value.toBoolean()
            Short::class -> value.toShort()
            Byte::class -> value.toByte()
            Char::class -> value.single()

            is KClass<*> -> {
                if (classifier.java.isEnum) {
                    classifier.java.enumConstants.first { it.toString() == value }
                } else {
                    json.decodeFromString(json.serializersModule.serializer(type), value)
                }
            }

            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

}
