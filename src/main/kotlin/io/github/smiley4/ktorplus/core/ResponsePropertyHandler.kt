package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.data.HandledResponseData
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.ktor.server.routing.RoutingCall

/**
 * Handles a single property of an outgoing response.
 */
interface ResponsePropertyHandler<TTypeDescriptorEntry : TypeDescriptorEntry> {

    /**
     * @return whether this handler can handle the given property (described by the given descriptor). The type of [descriptor] MUST match [TTypeDescriptorEntry]
     */
    fun appliesTo(descriptor: TypeDescriptorEntry): Boolean


    /**
     * Handles the given property of the outgoing response.
     * @return null or value for further handling (e.g. response body or status code).
     */
    fun handle(descriptor: TTypeDescriptorEntry, response: Any, call: RoutingCall): HandledResponseData?


    /**
     * Internal use only.
     * @see handle
     */
    fun unsafeHandle(descriptor: TypeDescriptorEntry, response: Any, call: RoutingCall): HandledResponseData? {
        @Suppress("UNCHECKED_CAST")
        return handle(descriptor as TTypeDescriptorEntry, response, call)
    }
}
