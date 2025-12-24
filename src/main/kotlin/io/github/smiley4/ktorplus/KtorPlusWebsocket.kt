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
inline fun <reified TConnection : Any, reified TMessage> Route.webSocket(
    path: String,
    actions: WebSocketActionHandler<TConnection, TMessage>.() -> Unit,
) {
    val actionHandler = WebSocketActionHandlerImpl<TConnection, TMessage>().apply(actions)

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
            when (frame) {
                is Frame.Text -> {
                    val receivedContent = KtorPlusConfig.json.decodeFromString<TMessage>(frame.readText())
                    actionHandler.handlerOnMessage(connection, receivedContent)
                }
                else -> Unit
            }
        }

        actionHandler.handlerOnClose(connection)
    }
}

interface WebSocketActionHandler<TConnection, TMessage> {
    fun onOpen(handler: (connection: TConnection) -> Unit)
    fun onClose(handler: (connection: TConnection) -> Unit)
    fun onMessage(handler: (connection: TConnection, message: TMessage) -> Unit)
}


class WebSocketActionHandlerImpl<TConnection, TMessage> : WebSocketActionHandler<TConnection, TMessage> {

    var handlerOnOpen: (connection: TConnection) -> Unit = {}
    var handlerOnClose: (connection: TConnection) -> Unit = {}
    var handlerOnMessage: (connection: TConnection, message: TMessage) -> Unit = { _, _ -> }

    override fun onOpen(handler: (connection: TConnection) -> Unit) {
        handlerOnOpen = handler
    }

    override fun onClose(handler: (connection: TConnection) -> Unit) {
        handlerOnClose = handler
    }

    override fun onMessage(handler: (connection: TConnection, message: TMessage) -> Unit) {
        handlerOnMessage = handler
    }

}