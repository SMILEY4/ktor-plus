package io.github.smiley4.ktorplus.response

import io.github.smiley4.ktorplus.core.ParameterEncoder
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.typedescriptor.CookieParameterDescriptor
import io.ktor.http.Cookie
import io.ktor.server.routing.RoutingCall

/**
 * Appends the cookie to the outgoing http response.
 */
class GenericCookieParameterResponseHandler(
    private val encoders: () -> List<ParameterEncoder<*>>
) : ResponsePropertyHandler<CookieParameterDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is CookieParameterDescriptor

    override fun handle(
        descriptor: CookieParameterDescriptor,
        response: Any,
        call: RoutingCall
    ): HandledResponseData? {
        when (val value = descriptor.property.call(response)) {
            is Cookie -> {
                call.response.cookies.append(value)
            }
            is io.github.smiley4.ktorplus.data.Cookie -> {
                value.toKtorCookie(descriptor.name).forEach {
                    call.response.cookies.append(it)
                }
            }
            else -> {
                val encoder = encoders()
                    .firstOrNull { it.canHandle(descriptor.property.returnType) }
                    ?: throw IllegalStateException("No decoder found for property ${descriptor.property.name} with type ${descriptor.property.returnType}")
                encoder.encodeUnsafe(value)?.also {
                    call.response.cookies.append(descriptor.property.name, it)
                }
            }
        }
        return null
    }

}
