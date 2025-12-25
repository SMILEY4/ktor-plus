package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptor
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall

/**
 * Handles http responses, i.e. builds responses from provided data.
 */
class ResponseHandler(private val propertyHandlers: List<ResponsePropertyHandler<*>>) {

    /**
     * Creates a new response as specified by [responseDescriptor] with data from [response].
     * [responseDescriptor] MUST match/describe [response].
     */
    suspend fun handle(responseDescriptor: TypeDescriptor, response: Any, call: RoutingCall) {

        val responseData = mutableListOf<HandledResponseData>()

        responseDescriptor.entries.forEach { propertyDescriptor ->
            propertyHandlers
                .find { it.appliesTo(propertyDescriptor) }
                ?.unsafeHandle(propertyDescriptor, response, call)
                ?.also { responseData.add(it) }
        }

        val body = responseData.filterIsInstance<HandledResponseBody>().lastOrNull()?.value
        val statusCode = responseData.filterIsInstance<HandledStatusCode>().lastOrNull()?.value

        if (body != null) {
            call.respond(statusCode ?: HttpStatusCode.Companion.OK, body)
        } else {
            call.respond(statusCode ?: HttpStatusCode.Companion.OK)
        }

    }

}
