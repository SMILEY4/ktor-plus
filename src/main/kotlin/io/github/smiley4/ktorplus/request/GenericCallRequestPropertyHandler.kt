package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.core.RequestPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CallDescriptor
import io.ktor.server.routing.RoutingCall

class GenericCallRequestPropertyHandler : RequestPropertyHandler<CallDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CallDescriptor

    override suspend fun handle(descriptor: CallDescriptor, call: RoutingCall): Map<String, Any?> {
        return mapOf(
            descriptor.property.name to call,
        )
    }

}
