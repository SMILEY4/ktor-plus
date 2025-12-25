package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.CookieParameter
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.typeOf

class CookieParameterDescriptor(
    val property: KCallable<*>,
    val name: String,
    val required: Boolean,
    val description: String?,
    val deprecated: Boolean?,
) : TypeDescriptorEntry

class CookieParameterAnalyzer : PropertyAnalyzer<CookieParameter, CookieParameterDescriptor> {

    override fun getAnnotationType() = typeOf<CookieParameter>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: CookieParameter) = CookieParameterDescriptor(
        property = property,
        name = annotation.name.ifEmpty { property.name },
        required = !property.returnType.isMarkedNullable,
        description = annotation.description.ifEmpty { null },
        deprecated = property.hasAnnotation<Deprecated>(),
    )

}
