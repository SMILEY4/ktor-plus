package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.BodyDescriptor
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KClass

/**
 * Provides access to the request body.
 */
class GenericBodyRequestPropertyHandler : RequestPropertyHandler<BodyDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is BodyDescriptor

    override suspend fun handle(descriptor: BodyDescriptor, call: RoutingCall): Map<String, Any?> {
        var value: Any?
        try {
            value = call.receive(TypeInfo(descriptor.property.returnType.classifier as KClass<*>))
        } catch (e: Exception) {
            throw IllegalArgumentException("Request body could not be deserialized, expected type ${descriptor.property.name}", e)
        }
        return mapOf(
            descriptor.property.name to value,
        )
    }

}
