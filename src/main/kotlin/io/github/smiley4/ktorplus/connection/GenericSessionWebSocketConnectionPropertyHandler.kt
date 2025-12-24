package io.github.smiley4.ktorplus.connection

import io.github.smiley4.ktorplus.core.WebSocketConnectionPropertyHandler
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CallDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.WebSocketSession

class GenericSessionWebSocketConnectionPropertyHandler : WebSocketConnectionPropertyHandler<CallDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CallDescriptor

    override suspend fun handle(descriptor: CallDescriptor, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {
        return mapOf(
            descriptor.property.name to session,
        )
    }

}
