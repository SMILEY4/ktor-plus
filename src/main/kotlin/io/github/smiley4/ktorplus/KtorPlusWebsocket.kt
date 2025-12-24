package io.github.smiley4.ktorplus

import io.github.smiley4.ktorplus.core.WebSocketConnectionHandler
import io.github.smiley4.ktorplus.core.PropertyAnalyzer
import io.github.smiley4.ktorplus.core.TypeAnalyzer
import io.github.smiley4.ktorplus.core.TypeDescriptorCache
import io.github.smiley4.ktorplus.core.TypeDescriptorCreator
import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import io.ktor.server.routing.Route
import io.ktor.utils.io.KtorDsl
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import java.lang.Exception
import kotlin.reflect.typeOf
import io.ktor.server.websocket.webSocket as ktorWebSocket

@KtorDsl
inline fun <reified TConnection : Any, reified TClientMessage, reified TServerMessage> Route.webSocket(
    path: String,
    context: WebSocketContext<TConnection, TServerMessage>? = null,
    actions: WebSocketActionHandler<TConnection, TClientMessage, TServerMessage>.() -> Unit,
) {
    val typeDescriptorCache = TypeDescriptorCache(
        @Suppress("UNCHECKED_CAST")
        TypeDescriptorCreator(
            KtorPlusConfig.typeAnalyzers as List<TypeAnalyzer<Annotation, TypeDescriptorEntry>>,
            KtorPlusConfig.propertyAnalyzers as List<PropertyAnalyzer<Annotation, TypeDescriptorEntry>>
        )
    )

    val actionHandler = WebSocketActionHandlerImpl<TConnection, TClientMessage, TServerMessage>().apply(actions)

    val webSocketConnectionHandler = WebSocketConnectionHandler(KtorPlusConfig.connectionHandlers)

    val context = context ?: WebSocketContext.create()

    ktorWebSocket(path, null) {
        val connectionDescriptor = typeDescriptorCache.get(typeOf<TConnection>())
        val connection = webSocketConnectionHandler.handle<TConnection>(connectionDescriptor, call)

        try {
            context.registerConnection(this, connection)
            actionHandler.handlerOnOpen(context, connection)

            for (frame in incoming) {
                try {
                    when (frame) {
                        is Frame.Text -> {
                            val receivedContent = KtorPlusConfig.json.decodeFromString<TClientMessage>(frame.readText())
                            actionHandler.handlerOnMessage(context, connection, receivedContent)
                        }
                        else -> Unit
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } finally {
            actionHandler.handlerOnClose(context, connection)
            context.deregisterConnection(connection)
        }
    }
}

interface WebSocketActionHandler<TConnection : Any, TClientMessage, TServerMessage> {
    fun onOpen(handler: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection) -> Unit)
    fun onClose(handler: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection) -> Unit)
    fun onMessage(handler: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection, message: TClientMessage) -> Unit)
}


class WebSocketActionHandlerImpl<TConnection : Any, TClientMessage, TServerMessage> : WebSocketActionHandler<TConnection, TClientMessage, TServerMessage> {

    var handlerOnOpen: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection) -> Unit = { _, _ -> }
    var handlerOnClose: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection) -> Unit = { _, _ -> }
    var handlerOnMessage: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection, message: TClientMessage) -> Unit = { _, _, _ -> }

    override fun onOpen(handler: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection) -> Unit) {
        handlerOnOpen = handler
    }

    override fun onClose(handler: suspend (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection) -> Unit) {
        handlerOnClose = handler
    }

    override fun onMessage(handler: suspend  (context: WebSocketContext<TConnection, TServerMessage>, connection: TConnection, message: TClientMessage) -> Unit) {
        handlerOnMessage = handler
    }

}


interface WebSocketContext<TConnection : Any, TServerMessage> {
    fun connections(): WebSocketConnectionSet<TConnection, TServerMessage>
    fun only(connection: TConnection): WebSocketConnectionSet<TConnection, TServerMessage>
    fun registerConnection(session: WebSocketSession, connection: TConnection)
    fun deregisterConnection(connection: TConnection)

    companion object {
        inline fun <TConnection : Any, reified TServerMessage> create(): WebSocketContext<TConnection, TServerMessage> {
            return WebSocketContextImpl(KtorPlusConfig.json.serializersModule.serializer<TServerMessage>())
        }
    }
}

interface WebSocketConnectionSet<TConnection : Any, TServerMessage> {
    fun filter(predicate: (TConnection) -> Boolean): WebSocketConnectionSet<TConnection, TServerMessage>
    fun toList(): List<TConnection>
    suspend fun send(message: TServerMessage)
    suspend fun close(reason: CloseReason)
}


class WebSocketContextImpl<TConnection : Any, TServerMessage>(
    private val serializer: KSerializer<TServerMessage>
) : WebSocketContext<TConnection, TServerMessage> {

    private val connections = mutableMapOf<TConnection, WebSocketSession>()

    override fun connections(): WebSocketConnectionSet<TConnection, TServerMessage> {
        return WebSocketConnectionSetImpl(this, connections.keys.toList())
    }

    override fun only(connection: TConnection): WebSocketConnectionSet<TConnection, TServerMessage> {
        return if (connections.containsKey(connection)) {
            WebSocketConnectionSetImpl(this, listOf(connection))
        } else {
            WebSocketConnectionSetImpl(this, emptyList())
        }
    }

    override fun registerConnection(session: WebSocketSession, connection: TConnection) {
        connections[connection] = session
    }

    override fun deregisterConnection(connection: TConnection) {
        connections.remove(connection)
    }

    suspend fun send(connection: TConnection, message: TServerMessage) {
        connections[connection]?.also { session ->
            session.send(KtorPlusConfig.json.encodeToString(serializer, message))
        }
    }

    suspend fun close(connection: TConnection, reason: CloseReason) {
        connections[connection]?.also { session ->
            session.close(reason)
        }
    }
}

class WebSocketConnectionSetImpl<TConnection : Any, TServerMessage>(
    private val context: WebSocketContextImpl<TConnection, TServerMessage>,
    private val connections: List<TConnection>
) :
    WebSocketConnectionSet<TConnection, TServerMessage> {

    override fun filter(predicate: (TConnection) -> Boolean): WebSocketConnectionSet<TConnection, TServerMessage> {
        return WebSocketConnectionSetImpl(context, connections.filter(predicate))
    }

    override fun toList(): List<TConnection> {
        return connections.toList()
    }

    override suspend fun send(message: TServerMessage) {
        connections.forEach { context.send(it, message) }
    }

    override suspend fun close(reason: CloseReason) {
        connections.forEach { context.close(it, reason) }
    }

}