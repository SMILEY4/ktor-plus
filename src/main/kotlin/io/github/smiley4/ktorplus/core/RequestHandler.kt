package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.data.TypeDescriptor
import io.ktor.server.routing.RoutingCall
import kotlin.collections.get
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

/**
 * Handles http requests. i.e. extracts required information from incoming calls.
 */
class RequestHandler(private val propertyHandlers: List<RequestPropertyHandler<*>>) {

    /**
     * Handle a new request - extracts information from the call and
     * returns it as a new instance of [T] (as described by the given descriptor).
     * @param requestDescriptor the description of the request format
     * @param call the ktor http call
     */
    suspend inline fun <reified T : Any> handle(requestDescriptor: TypeDescriptor, call: RoutingCall): T {
        return createInstanceFromMap<T>(handleInternal(requestDescriptor, call))
    }


    /**
     * Internal use only.
     * @see handle
     */
    suspend fun handleInternal(requestDescriptor: TypeDescriptor, call: RoutingCall): Map<String, Any?> {

        val requestData = mutableMapOf<String, Any?>()

        requestDescriptor.entries.forEach { propertyDescriptor ->
            propertyHandlers
                .find { it.appliesTo(propertyDescriptor) }
                ?.unsafeHandle(propertyDescriptor, call)
                ?.also { requestData.putAll(it) }
        }

        return requestData
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
