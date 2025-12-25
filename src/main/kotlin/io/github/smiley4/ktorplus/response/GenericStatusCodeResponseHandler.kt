package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.ResponseDescriptor
import io.ktor.server.routing.RoutingCall

/**
 * Appends the status code to the outgoing http response.
 */
class GenericStatusCodeResponseHandler : ResponsePropertyHandler<ResponseDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is ResponseDescriptor

    override fun handle(
        descriptor: ResponseDescriptor,
        response: Any,
        call: RoutingCall
    ): HandledResponseData {
        return HandledStatusCode(
            descriptor.statusCode
        )
    }

}
