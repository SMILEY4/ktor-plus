package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.QueryParameter
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.typeOf

class QueryParameterDescriptor(
    val property: KCallable<*>,
    val name: String,
    val required: Boolean,
    val description: String?,
    val deprecated: Boolean?,
) : TypeDescriptorEntry

class QueryParameterAnalyzer : PropertyAnalyzer<QueryParameter, QueryParameterDescriptor> {

    override fun getAnnotationType() = typeOf<QueryParameter>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: QueryParameter) = QueryParameterDescriptor(
        property = property,
        name = annotation.name.ifEmpty { property.name },
        description = annotation.description.ifEmpty { null },
        deprecated = property.hasAnnotation<Deprecated>(),
        required = !property.returnType.isMarkedNullable,
    )

}
