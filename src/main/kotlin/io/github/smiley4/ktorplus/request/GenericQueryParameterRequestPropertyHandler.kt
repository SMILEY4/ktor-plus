package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.core.ParameterDecoder
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.QueryParameterDescriptor
import io.ktor.server.routing.RoutingCall

/**
 * Provides access to a url query parameter from the incoming http request.
 */
class GenericQueryParameterRequestPropertyHandler(
    private val decoders: () -> List<ParameterDecoder<*>>
) : RequestPropertyHandler<QueryParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is QueryParameterDescriptor

    override suspend fun handle(descriptor: QueryParameterDescriptor, call: RoutingCall): Map<String, Any?> {
        val rawValue = getRawValue(descriptor, call)
        val value = decode(rawValue, descriptor)
        return mapOf(descriptor.property.name to value)
    }

    fun getRawValue(descriptor: QueryParameterDescriptor, call: RoutingCall): String? {
        val value = call.parameters[descriptor.name]
        if (value == null && descriptor.required) {
            throw IllegalArgumentException("Missing query parameter with name ${descriptor.name}")
        }
        return value
    }

    fun decode(rawValue: String?, descriptor: QueryParameterDescriptor): Any? {
        val decoder = decoders().firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException(
                "No decoder found for property ${descriptor.property.name} with type ${descriptor.property.returnType}"
            )
        return decoder.decode(rawValue, descriptor.property.returnType, KtorPlusConfig.json)
    }

}
