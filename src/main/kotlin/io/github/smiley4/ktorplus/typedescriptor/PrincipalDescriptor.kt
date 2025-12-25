package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.Principal
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class PrincipalDescriptor(
    val property: KCallable<*>,
    val required: Boolean,
) : TypeDescriptorEntry

class PrincipalAnalyzer : PropertyAnalyzer<Principal, PrincipalDescriptor> {

    override fun getAnnotationType() = typeOf<Principal>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: Principal) = PrincipalDescriptor(
        property = property,
        required = !property.returnType.isMarkedNullable,
    )

}
