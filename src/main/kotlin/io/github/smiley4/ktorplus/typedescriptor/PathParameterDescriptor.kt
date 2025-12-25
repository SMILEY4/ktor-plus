package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.PathParameter
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.typeOf

class PathParameterDescriptor(
    val property: KCallable<*>,
    val name: String,
    val required: Boolean,
    val description: String?,
    val deprecated: Boolean?,
) : TypeDescriptorEntry

class PathParameterAnalyzer : PropertyAnalyzer<PathParameter, PathParameterDescriptor> {

    override fun getAnnotationType() = typeOf<PathParameter>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: PathParameter) = PathParameterDescriptor(
        property = property,
        name = annotation.name.ifEmpty { property.name },
        required = !property.returnType.isMarkedNullable,
        description = annotation.description.ifEmpty { null },
        deprecated = property.hasAnnotation<Deprecated>(),
    )

}
