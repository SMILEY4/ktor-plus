package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.core.ParameterEncoder
import io.github.smiley4.ktorplus.core.ResponsePropertyHandler
import io.github.smiley4.ktorplus.data.HandledResponseData
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.HeaderParameterDescriptor
import io.ktor.server.routing.RoutingCall

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
            ?: throw IllegalStateException("No encoder found for type ${descriptor.property.returnType}")
        encoder.encodeUnsafe(descriptor.property.call(response))?.also {
            call.response.headers.append(descriptor.name, it)
        }
        return null
    }

}
