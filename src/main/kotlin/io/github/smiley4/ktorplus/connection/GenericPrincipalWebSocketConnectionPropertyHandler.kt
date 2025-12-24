package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.core.WebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.PrincipalDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.websocket.WebSocketSession
import kotlin.reflect.KClass

class GenericPrincipalWebSocketConnectionPropertyHandler : WebSocketConnectionPropertyHandler<PrincipalDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is PrincipalDescriptor

    override suspend fun handle(descriptor: PrincipalDescriptor, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {
        val value = call.authentication.principal(null, descriptor.property.returnType.classifier as KClass<*>)
        return mapOf(
            descriptor.property.name to value,
        )
    }

}
