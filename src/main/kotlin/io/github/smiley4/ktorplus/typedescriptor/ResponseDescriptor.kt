package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.data.Response
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.typeOf

class ResponseDescriptor(
    val statusCode: HttpStatusCode,
    val description: String?
) : TypeDescriptorEntry

class ResponseAnalyzer : TypeAnalyzer<Response, ResponseDescriptor> {

    override fun getAnnotationType() = typeOf<Response>()

    override fun process(type: KClass<*>, annotation: Response) = ResponseDescriptor(
        statusCode = HttpStatusCode.fromValue(annotation.statusCode),
        description = type.findAnnotation<Response>()!!.description.ifEmpty { null },
    )

}
