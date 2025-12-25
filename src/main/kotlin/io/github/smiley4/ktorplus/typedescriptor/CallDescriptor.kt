package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.Call
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class CallDescriptor(
    val property: KCallable<*>,
) : TypeDescriptorEntry

class CallAnalyzer : PropertyAnalyzer<Call, CallDescriptor> {

    override fun getAnnotationType() = typeOf<Call>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: Call) = CallDescriptor(
        property = property
    )

}
