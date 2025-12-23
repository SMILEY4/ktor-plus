package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.core.RequestPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.PrincipalDescriptor
import io.ktor.server.auth.authentication
import io.ktor.server.routing.RoutingCall
import kotlin.reflect.KClass

class GenericPrincipalRequestPropertyHandler : RequestPropertyHandler<PrincipalDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is PrincipalDescriptor

    override suspend fun handle(descriptor: PrincipalDescriptor, call: RoutingCall): Map<String, Any?> {
        val value = call.authentication.principal(null, descriptor.property.returnType.classifier as KClass<*>)
        return mapOf(
            descriptor.property.name to value,
        )
    }

}
