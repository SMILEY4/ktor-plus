package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.data.Request
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class RequestDescriptor : TypeDescriptorEntry

class RequestAnalyzer : TypeAnalyzer<Request, RequestDescriptor> {

    override fun getAnnotationType() = typeOf<Request>()

    override fun process(type: KClass<*>, annotation: Request) = RequestDescriptor()

}
