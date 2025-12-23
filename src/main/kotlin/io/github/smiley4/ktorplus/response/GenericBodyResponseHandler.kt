package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.core.ResponsePropertyHandler
import io.github.smiley4.ktorplus.data.HandledResponseBody
import io.github.smiley4.ktorplus.data.HandledResponseData
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.BodyDescriptor
import io.ktor.server.routing.RoutingCall

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
