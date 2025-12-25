package io.github.smiley4.ktorplus.typedescriptor

import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.data.Connection
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class WebSocketConnectionDescriptor : TypeDescriptorEntry

class WebSocketConnectionAnalyzer : TypeAnalyzer<Connection, WebSocketConnectionDescriptor> {

    override fun getAnnotationType() = typeOf<Connection>()

    override fun process(type: KClass<*>, annotation: Connection) = WebSocketConnectionDescriptor()

}
