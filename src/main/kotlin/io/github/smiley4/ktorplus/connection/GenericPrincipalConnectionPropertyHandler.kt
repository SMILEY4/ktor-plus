package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.core.ConnectionPropertyHandler
import io.github.smiley4.ktorplus.core.RequestPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.PrincipalDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.routing.RoutingCall
import kotlin.reflect.KClass

class GenericPrincipalConnectionPropertyHandler : ConnectionPropertyHandler<PrincipalDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is PrincipalDescriptor

    override suspend fun handle(descriptor: PrincipalDescriptor, call: ApplicationCall): Map<String, Any?> {
        val value = call.authentication.principal(null, descriptor.property.returnType.classifier as KClass<*>)
        return mapOf(
            descriptor.property.name to value,
        )
    }

}
