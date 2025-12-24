package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.core.ConnectionPropertyHandler
import io.github.smiley4.ktorplus.core.ParameterDecoder
import io.github.smiley4.ktorplus.core.RequestPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.HeaderParameterDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.routing.RoutingCall

class GenericHeaderParameterConnectionPropertyHandler(
    private val decoders: () -> List<ParameterDecoder<*>>
) : ConnectionPropertyHandler<HeaderParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is HeaderParameterDescriptor

    override suspend fun handle(descriptor: HeaderParameterDescriptor, call: ApplicationCall): Map<String, Any?> {
        val rawValue = getRawValue(descriptor, call)
        val value = decode(rawValue, descriptor)
        return mapOf(descriptor.property.name to value)
    }

    fun getRawValue(descriptor: HeaderParameterDescriptor, call: ApplicationCall): String? {
        val value = call.request.header(descriptor.name)
        if (value == null && descriptor.required) {
            throw IllegalArgumentException("Missing header ${descriptor.name}")
        }
        return value
    }

    fun decode(rawValue: String?, descriptor: HeaderParameterDescriptor): Any? {
        val decoder = decoders().firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException("No decoder found for type ${descriptor.property.returnType}")
        return decoder.decode(rawValue, descriptor.property.returnType, KtorPlusConfig.json)
    }

}
