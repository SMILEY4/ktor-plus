package io.github.smiley4.ktorplus.websocketconnection

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptor
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.WebSocketSession
import kotlin.collections.get
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

/**
 * Handles http connections. i.e. extracts required information from incoming calls.
 */
class WebSocketConnectionHandler(private val connectionHandlers: List<WebSocketConnectionPropertyHandler<*>>) {

    /**
     * Handle a new connection - extracts information from the call and
     * returns it as a new instance of [T] (as described by the given descriptor).
     * @param requestDescriptor the description of the connection format
     * @param call the ktor http call
     */
    suspend inline fun <reified T : Any> handle(requestDescriptor: TypeDescriptor, call: ApplicationCall, session: WebSocketSession): T {
        return createInstanceFromMap<T>(handleInternal(requestDescriptor, call, session))
    }


    /**
     * Internal use only.
     * @see handle
     */
    suspend fun handleInternal(requestDescriptor: TypeDescriptor, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {

        val connectionData = mutableMapOf<String, Any?>()

        requestDescriptor.entries.forEach { propertyDescriptor ->
            connectionHandlers
                .find { it.appliesTo(propertyDescriptor) }
                ?.unsafeHandle(propertyDescriptor, call, session)
                ?.also { connectionData.putAll(it) }
        }

        return connectionData
    }


    /**
     * Create a new instance of [T] from the given map of values.
     * The keys must match the names of the properties of [T].
     * The values must match the actual values of the properties.
     */
    inline fun <reified T : Any> createInstanceFromMap(values: Map<String, Any?>): T {
        val constructor = T::class.primaryConstructor
            ?: throw IllegalArgumentException("Class ${T::class.simpleName} has no primary constructor")
        val args = constructor.parameters.associateWith { param -> values[param.name] }
        constructor.isAccessible = true
        return constructor.callBy(args)
    }

}