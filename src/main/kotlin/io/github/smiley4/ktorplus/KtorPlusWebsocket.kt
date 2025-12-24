package io.github.smiley4.ktorplus

import io.github.smiley4.ktorplus.core.ConnectionHandler
import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.core.TypeDescriptorCache
import io.github.smiley4.ktorplus.core.TypeDescriptorCreator
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.ktor.server.routing.Route
import io.ktor.utils.io.KtorDsl
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlin.reflect.typeOf
import io.ktor.server.websocket.webSocket as ktorWebSocket

@KtorDsl
inline fun <reified TConnection : Any> Route.webSocket(
    path: String,
    actions: WebSocketActionHandler<TConnection>.() -> Unit,
) {
    val actionHandler = WebSocketActionHandlerImpl<TConnection>().apply(actions)

    val typeDescriptorCache = TypeDescriptorCache(
        @Suppress("UNCHECKED_CAST")
        TypeDescriptorCreator(
            KtorPlusConfig.typeAnalyzers as List<TypeAnalyzer<Annotation, TypeDescriptorEntry>>,
            KtorPlusConfig.propertyAnalyzers as List<PropertyAnalyzer<Annotation, TypeDescriptorEntry>>
        )
    )

    val connectionHandler = ConnectionHandler(KtorPlusConfig.connectionHandlers)

    ktorWebSocket(path, null) {
        val connectionDescriptor = typeDescriptorCache.get(typeOf<TConnection>())
        val connection = connectionHandler.handle<TConnection>(connectionDescriptor, call)

        actionHandler.handlerOnOpen(connection)

        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedContent = frame.readText()
            actionHandler.handlerOnMessage(connection, receivedContent)
        }

        actionHandler.handlerOnClose(connection)
    }
}

interface WebSocketActionHandler<TConnection> {
    fun onOpen(handler: (connection: TConnection) -> Unit)
    fun onClose(handler: (connection: TConnection) -> Unit)
    fun onMessage(handler: (connection: TConnection, message: String) -> Unit)
}


class WebSocketActionHandlerImpl<TConnection> : WebSocketActionHandler<TConnection> {

    var handlerOnOpen: (connection: TConnection) -> Unit = {}
    var handlerOnClose: (connection: TConnection) -> Unit = {}
    var handlerOnMessage: (connection: TConnection, message: String) -> Unit = { _, _ -> }

    override fun onOpen(handler: (connection: TConnection) -> Unit) {
        handlerOnOpen = handler
    }

    override fun onClose(handler: (connection: TConnection) -> Unit) {
        handlerOnClose = handler
    }

    override fun onMessage(handler: (connection: TConnection, message: String) -> Unit) {
        handlerOnMessage = handler
    }

}