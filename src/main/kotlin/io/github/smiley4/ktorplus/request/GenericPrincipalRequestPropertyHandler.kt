package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.PrincipalDescriptor
import io.ktor.server.auth.authentication
import io.ktor.server.routing.RoutingCall
import kotlin.reflect.KClass

/**
 * Provides access to the ktor authentication principal with the specified type.
 */
class GenericPrincipalRequestPropertyHandler : RequestPropertyHandler<PrincipalDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is PrincipalDescriptor

    override suspend fun handle(descriptor: PrincipalDescriptor, call: RoutingCall): Map<String, Any?> {
        val value = call.authentication.principal(null, descriptor.property.returnType.classifier as KClass<*>)
        if(descriptor.required && value == null) {
            throw IllegalArgumentException("Missing principal of type ${descriptor.property.returnType.javaClass.name}")
        }
        return mapOf(
            descriptor.property.name to value,
        )
    }

}
