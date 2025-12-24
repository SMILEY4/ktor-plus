package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.core.WebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.core.ParameterDecoder
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.PathParameterDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.WebSocketSession

class GenericPathParameterWebSocketConnectionPropertyHandler(
    private val decoders: () -> List<ParameterDecoder<*>>
) : WebSocketConnectionPropertyHandler<PathParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is PathParameterDescriptor

    override suspend fun handle(descriptor: PathParameterDescriptor, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {
        val rawValue = getRawValue(descriptor, call)
        val value = decode(rawValue, descriptor)
        return mapOf(descriptor.property.name to value)
    }

    fun getRawValue(descriptor: PathParameterDescriptor, call: ApplicationCall): String {
        val value = call.parameters[descriptor.name]
        if (value == null) {
            throw IllegalArgumentException("Missing path parameter ${descriptor.name}")
        }
        return value
    }

    fun decode(rawValue: String?, descriptor: PathParameterDescriptor): Any? {
        val decoder = decoders().firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException("No decoder found for type ${descriptor.property.returnType}")
        return decoder.decode(rawValue, descriptor.property.returnType, KtorPlusConfig.json)
    }

}
