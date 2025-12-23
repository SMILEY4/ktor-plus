package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.core.RequestPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.BodyDescriptor
import io.ktor.server.request.receive
import io.ktor.server.routing.RoutingCall
import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KClass

class GenericBodyRequestPropertyHandler : RequestPropertyHandler<BodyDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is BodyDescriptor

    override suspend fun handle(descriptor: BodyDescriptor, call: RoutingCall): Map<String, Any?> {
        return mapOf(
            descriptor.property.name to call.receive(TypeInfo(descriptor.property.returnType.classifier as KClass<*>)),
        )
    }

}
