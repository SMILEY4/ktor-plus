package io.github.smiley4.ktorplus.openapi

import io.github.smiley4.ktorplus.core.TypeDescriptorCache
import io.github.smiley4.ktorplus.data.Response
import io.github.smiley4.ktorplus.typedescriptor.BodyDescriptor
import io.github.smiley4.ktorplus.typedescriptor.CookieParameterDescriptor
import io.github.smiley4.ktorplus.typedescriptor.HeaderParameterDescriptor
import io.github.smiley4.ktorplus.typedescriptor.PathParameterDescriptor
import io.github.smiley4.ktorplus.typedescriptor.QueryParameterDescriptor
import io.github.smiley4.ktorplus.typedescriptor.ResponseDescriptor
import io.github.smiley4.ktoropenapi.config.RouteConfig
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.hasAnnotation

class KtorPlusRouteOpenApiHandler(private val typeDescriptors: TypeDescriptorCache) {

    fun setup(routeConfig: RouteConfig, requestType: KType, rootResponseType: KType, authenticationNames: Set<String>) {

        // security
        routeConfig.securitySchemeNames(authenticationNames)

        // request
        val requestTypeDescriptor = typeDescriptors.get(requestType)
        routeConfig.request {
            // path parameter
            requestTypeDescriptor.entries.filterIsInstance<PathParameterDescriptor>().forEach { parameter ->
                pathParameter(parameter.name, parameter.property.returnType) {
                    description = parameter.description
                }
            }
            // query parameter
            requestTypeDescriptor.entries.filterIsInstance<QueryParameterDescriptor>().forEach { parameter ->
                queryParameter(parameter.name, parameter.property.returnType) {
                    description = parameter.description
                }
            }
            // header parameter
            requestTypeDescriptor.entries.filterIsInstance<HeaderParameterDescriptor>().forEach { parameter ->
                headerParameter(parameter.name, parameter.property.returnType) {
                    description = parameter.description
                }
            }
            // cookie parameter
            requestTypeDescriptor.entries.filterIsInstance<CookieParameterDescriptor>().forEach { parameter ->
                cookieParameter(parameter.name, parameter.property.returnType) {
                    description = parameter.description
                }
            }
            // request body
            requestTypeDescriptor.entries.filterIsInstance<BodyDescriptor>().firstOrNull()?.also { body ->
                body(body.property.returnType) {
                    description = body.description
                }
            }
        }

        val responseTypes = collectRelevantResponseTypes(rootResponseType.classifier as KClass<*>)

        routeConfig.response {

            val responseTypeDescriptors = responseTypes
                .map { typeDescriptors.get(it) }
                .map { it.entries.filterIsInstance<ResponseDescriptor>().firstOrNull()?.statusCode to it }

            responseTypeDescriptors.sortedBy { it.first }.forEach { (statusCode, responseTypeDescriptor) ->

                // todo:
                //   If multiple responses have the same status code, only one will be displayed.
                //   Note: Swagger can only handle numeric status codes (afaik).
                val statusCode = (statusCode?.value ?: 0).toString()

                // response
                code(statusCode) {
                    // response description
                    description = responseTypeDescriptor.entries.filterIsInstance<ResponseDescriptor>().firstOrNull()?.description
                    // header
                    responseTypeDescriptor.entries.filterIsInstance<HeaderParameterDescriptor>().forEach { parameter ->
                        header(parameter.name, parameter.property.returnType) {
                            description = parameter.description
                        }
                    }
                    // response body
                    responseTypeDescriptor.entries.filterIsInstance<BodyDescriptor>().firstOrNull()?.also { body ->
                        body(body.property.returnType) {
                            description = body.description
                        }
                    }
                    // cookie (not supported by ktor-openapi-tools)
                }
            }

        }

    }

    fun collectRelevantResponseTypes(type: KClass<*>): List<KClass<*>> {
        val responseTypes = mutableListOf<KClass<*>>()

        val openTypes = mutableSetOf(type)
        while (openTypes.isNotEmpty()) {
            val current = openTypes.first().also { openTypes.remove(it) }
            if (current.hasAnnotation<Response>()) {
                responseTypes.add(current)
            }
            openTypes.addAll(current.sealedSubclasses)
        }

        return responseTypes
    }

}

