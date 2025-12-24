package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.core.ConnectionPropertyHandler
import io.github.smiley4.ktorplus.core.RequestPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CallDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.RoutingCall

class GenericCallConnectionPropertyHandler : ConnectionPropertyHandler<CallDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CallDescriptor

    override suspend fun handle(descriptor: CallDescriptor, call: ApplicationCall): Map<String, Any?> {
        return mapOf(
            descriptor.property.name to call,
        )
    }

}
