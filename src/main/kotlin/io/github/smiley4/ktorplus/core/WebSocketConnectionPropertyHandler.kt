package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.ktor.server.application.ApplicationCall
import io.ktor.websocket.WebSocketSession

/**
 * Handles a single property of a websocket connection.
 */
interface WebSocketConnectionPropertyHandler<TTypeDescriptorEntry : TypeDescriptorEntry> {

    /**
     * @return whether this handler can handle the given property (described by the given descriptor).
     * The type of [descriptor] MUST match [TTypeDescriptorEntry]
     */
    fun appliesTo(descriptor: TypeDescriptorEntry): Boolean


    /**
     * Handles the given property of the websocket connection.
     * @return the values for properties to set. Keys must match names of actual [kotlin.reflect.KCallable]s.
     */
    suspend fun handle(descriptor: TTypeDescriptorEntry, call: ApplicationCall, session: WebSocketSession): Map<String, Any?>


    /**
     * Internal use only.
     * @see handle
     */
    suspend fun unsafeHandle(descriptor: TypeDescriptorEntry, call: ApplicationCall, session: WebSocketSession): Map<String, Any?> {
        @Suppress("UNCHECKED_CAST")
        return handle(descriptor as TTypeDescriptorEntry, call, session)
    }
}
