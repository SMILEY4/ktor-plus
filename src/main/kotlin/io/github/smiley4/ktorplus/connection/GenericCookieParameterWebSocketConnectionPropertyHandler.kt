package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.core.WebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.core.ParameterDecoder
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CookieParameterDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.WebSocketSession

class GenericCookieParameterWebSocketConnectionPropertyHandler(
    private val decoders: () -> List<ParameterDecoder<*>>
) : WebSocketConnectionPropertyHandler<CookieParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CookieParameterDescriptor

    override suspend fun handle(descriptor: CookieParameterDescriptor, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {
        val rawValue = getRawValue(descriptor, call)
        val value = decode(rawValue, descriptor)
        return mapOf(descriptor.property.name to value)
    }

    private fun getRawValue(descriptor: CookieParameterDescriptor, call: ApplicationCall): String? {
        val value = call.request.cookies[descriptor.name]
        if (value == null && descriptor.required) {
            throw IllegalArgumentException("Missing cookie ${descriptor.name}")
        }
        return value
    }

    private fun decode(rawValue: String?, descriptor: CookieParameterDescriptor): Any? {
        val decoder = decoders().firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException("No decoder found for type ${descriptor.property.returnType}")
        return decoder.decode(rawValue, descriptor.property.returnType, KtorPlusConfig.json)
    }

}
