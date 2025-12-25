package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.data.Call
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.github.smiley4.ktorplus.data.WebSocketSession
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class WebSocketSessionDescriptor(
    val property: KCallable<*>,
) : TypeDescriptorEntry

class WebSocketSessionAnalyzer : PropertyAnalyzer<WebSocketSession, WebSocketSessionDescriptor> {

    override fun getAnnotationType() = typeOf<WebSocketSession>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: WebSocketSession) = WebSocketSessionDescriptor(
        property = property
    )

}
