package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.core.ResponsePropertyHandler
import io.github.smiley4.ktorplus.data.HandledResponseData
import io.github.smiley4.ktorplus.data.HandledStatusCode
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.ResponseDescriptor
import io.ktor.server.routing.RoutingCall

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
