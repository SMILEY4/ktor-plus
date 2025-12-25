package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CallDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.RoutingCall
import kotlin.reflect.typeOf

/**
 * Provides access to the raw ktor [ApplicationCall].
 */
class GenericCallRequestPropertyHandler : RequestPropertyHandler<CallDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CallDescriptor

    override suspend fun handle(descriptor: CallDescriptor, call: RoutingCall): Map<String, Any?> {
        if (descriptor.property.returnType !== typeOf<RoutingCall>()) {
            throw IllegalArgumentException("Invalid type of property ${descriptor.property.name}, expected ${RoutingCall::class.qualifiedName}")
        }
        return mapOf(
            descriptor.property.name to call,
        )
    }

}
