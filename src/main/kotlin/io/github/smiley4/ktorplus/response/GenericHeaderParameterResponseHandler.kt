package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.core.ParameterEncoder
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.HeaderParameterDescriptor
import io.ktor.server.routing.RoutingCall

/**
 * Appends the header to the outgoing http response.
 */
class GenericHeaderParameterResponseHandler(
    private val encoders: () -> List<ParameterEncoder<*>>
) : ResponsePropertyHandler<HeaderParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is HeaderParameterDescriptor

    override fun handle(
        descriptor: HeaderParameterDescriptor,
        response: Any,
        call: RoutingCall
    ): HandledResponseData? {
        val encoder = encoders()
            .firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException("No decoder found for property ${descriptor.property.name} with type ${descriptor.property.returnType}")
        encoder.encodeUnsafe(descriptor.property.call(response))?.also {
            call.response.headers.append(descriptor.name, it)
        }
        return null
    }

}
