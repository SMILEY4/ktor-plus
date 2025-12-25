package io.github.smiley4.ktorplus.request

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.ktor.server.routing.RoutingCall

/**
 * Handles a single property of an incoming request.
 */
interface RequestPropertyHandler<TTypeDescriptorEntry : TypeDescriptorEntry> {

    /**
     * @return whether this handler can handle the given property (described by the given descriptor).
     * The type of [descriptor] MUST match [TTypeDescriptorEntry]
     */
    fun appliesTo(descriptor: TypeDescriptorEntry): Boolean


    /**
     * Handles the given property of the incoming request.
     * @return the values for properties to set. Keys must match names of actual [kotlin.reflect.KCallable]s.
     */
    suspend fun handle(descriptor: TTypeDescriptorEntry, call: RoutingCall): Map<String, Any?>


    /**
     * Internal use only.
     * @see handle
     */
    suspend fun unsafeHandle(descriptor: TypeDescriptorEntry, call: RoutingCall): Map<String, Any?> {
        @Suppress("UNCHECKED_CAST")
        return handle(descriptor as TTypeDescriptorEntry, call)
    }
}