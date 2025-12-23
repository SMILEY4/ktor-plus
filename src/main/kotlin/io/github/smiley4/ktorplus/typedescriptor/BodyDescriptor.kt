package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.Body
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class BodyDescriptor(
    val property: KCallable<*>,
    val description: String?,
    val required: Boolean,
) : TypeDescriptorEntry

class BodyAnalyzer : PropertyAnalyzer<Body, BodyDescriptor> {

    override fun getAnnotationType() = typeOf<Body>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: Body) = BodyDescriptor(
        property = property,
        required = !property.returnType.isMarkedNullable,
        description = annotation.description.ifEmpty { null },
    )

}
