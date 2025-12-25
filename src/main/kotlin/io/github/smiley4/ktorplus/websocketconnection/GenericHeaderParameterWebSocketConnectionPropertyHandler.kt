package io.github.smiley4.ktorplus.websocketconnection

import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.core.ParameterDecoder
import io.github.smiley4.ktorplus.typedescriptor.HeaderParameterDescriptor
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.websocket.WebSocketSession

/**
 * Provides access to a header parameter from the incoming http request.
 */
class GenericHeaderParameterWebSocketConnectionPropertyHandler(
    private val decoders: () -> List<ParameterDecoder<*>>
) : WebSocketConnectionPropertyHandler<HeaderParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is HeaderParameterDescriptor

    override suspend fun handle(
        descriptor: HeaderParameterDescriptor,
        call: ApplicationCall,
        session: WebSocketSession
    ): Map<String, Any?> {
        val rawValue = getRawValue(descriptor, call)
        val value = decode(rawValue, descriptor)
        return mapOf(descriptor.property.name to value)
    }

    fun getRawValue(descriptor: HeaderParameterDescriptor, call: ApplicationCall): String? {
        val value = call.request.header(descriptor.name)
        if (value == null && descriptor.required) {
            throw IllegalArgumentException("Missing header with name ${descriptor.name}")
        }
        return value
    }

    fun decode(rawValue: String?, descriptor: HeaderParameterDescriptor): Any? {
        val decoder = decoders().firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException(
                "No decoder found for property ${descriptor.property.name} with type ${descriptor.property.returnType}"
            )
        return decoder.decode(rawValue, descriptor.property.returnType, KtorPlusConfig.json)
    }

}
