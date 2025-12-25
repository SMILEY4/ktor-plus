package io.github.smiley4.ktorplus

import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.documentation
import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.request.RequestHandler
import io.github.smiley4.ktorplus.response.ResponseHandler
import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.core.TypeDescriptorCache
import io.github.smiley4.ktorplus.core.TypeDescriptorCreator
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import io.github.smiley4.ktorplus.openapi.KtorPlusRouteOpenApiHandler
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.method
import io.ktor.server.routing.route
import io.ktor.utils.io.KtorDsl
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.get(
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return methodPlus<TRequest, TResponse>(HttpMethod.Get, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.get(
    path: String,
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return routePlus<TRequest, TResponse>(path, HttpMethod.Get, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.post(
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return methodPlus<TRequest, TResponse>(HttpMethod.Post, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.post(
    path: String,
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return documentation(documentation) {
        routePlus<TRequest, TResponse>(path, HttpMethod.Post, documentation, body)
    }
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.put(
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return methodPlus<TRequest, TResponse>(HttpMethod.Put, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.put(
    path: String,
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return routePlus<TRequest, TResponse>(path, HttpMethod.Put, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.delete(
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return methodPlus<TRequest, TResponse>(HttpMethod.Delete, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.delete(
    path: String,
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return routePlus<TRequest, TResponse>(path, HttpMethod.Delete, documentation, body)
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.routePlus(
    path: String,
    method: HttpMethod,
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {
    return route(path) {
        methodPlus<TRequest, TResponse>(method, documentation, body)
    }
}


@KtorDsl
inline fun <reified TRequest : Any, reified TResponse : Any> Route.methodPlus(
    method: HttpMethod,
    noinline documentation: RouteConfig.() -> Unit = {},
    crossinline body: suspend (request: TRequest) -> TResponse
): Route {

    val typeDescriptorCache = TypeDescriptorCache(
        @Suppress("UNCHECKED_CAST")
        TypeDescriptorCreator(
            KtorPlusConfig.typeAnalyzers as List<TypeAnalyzer<Annotation, TypeDescriptorEntry>>,
            KtorPlusConfig.propertyAnalyzers as List<PropertyAnalyzer<Annotation, TypeDescriptorEntry>>
        )
    )
    val requestHandler = RequestHandler(KtorPlusConfig.requestHandlers)
    val responseHandler = ResponseHandler(KtorPlusConfig.responseHandlers)

    val authenticationNames = mutableSetOf<String>()
    var currentRoutingNode: Route? = this
    while (currentRoutingNode != null) {
        currentRoutingNode = currentRoutingNode.parent
        if (currentRoutingNode is RoutingNode) {
            val selector = currentRoutingNode.selector
            if (selector is AuthenticationRouteSelector) {
                authenticationNames.addAll(selector.names.filterNotNull())
            }
        }
    }

    return documentation(documentation) {
        documentation({
            KtorPlusRouteOpenApiHandler(typeDescriptorCache).setup(
                this,
                typeOf<TRequest>(),
                typeOf<TResponse>(),
                authenticationNames
            )
        }) {
            method(method) {
                handle {
                    val requestDescriptor = typeDescriptorCache.get(typeOf<TRequest>())
                    val request = requestHandler.handle<TRequest>(requestDescriptor, call)
                    val response = body(request)
                    val responseDescriptor = typeDescriptorCache.get(response::class.starProjectedType)
                    responseHandler.handle(responseDescriptor, response, call)
                }
            }
        }
    }
}
