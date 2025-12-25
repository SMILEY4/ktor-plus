package io.github.smiley4.ktorplus.websocketconnection

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CallDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.WebSocketSession
import kotlin.reflect.typeOf

/**
 * Provides access to the raw ktor [ApplicationCall].
 */
class GenericCallWebSocketConnectionPropertyHandler : WebSocketConnectionPropertyHandler<CallDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CallDescriptor

    override suspend fun handle(descriptor: CallDescriptor, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {
        if (descriptor.property.returnType !== typeOf<ApplicationCall>()) {
            throw IllegalArgumentException(
                "Invalid type of property ${descriptor.property.name}, expected ${ApplicationCall::class.qualifiedName}"
            )
        }
        return mapOf(
            descriptor.property.name to call,
        )
    }

}
