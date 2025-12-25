package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.BodyDescriptor
import io.ktor.server.routing.RoutingCall

/**
 * Appends the response body to the outgoing http response.
 */
class GenericBodyResponseHandler : ResponsePropertyHandler<BodyDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is BodyDescriptor

    override fun handle(
        descriptor: BodyDescriptor,
        response: Any,
        call: RoutingCall
    ): HandledResponseData {
        return HandledResponseBody(
            descriptor.property.call(response)
        )
    }

}
