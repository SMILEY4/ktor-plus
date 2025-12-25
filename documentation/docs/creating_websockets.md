# Creating WebSocket

## Defining a WebSocket Route

WebSocket routes are declared using the webSocket function with three type parameters:

````kotlin
webSocket<ChatConnection, ClientChatMessage, ServerChatMessage>("chat/{roomId}") {
    // handlers
}
````

- `ChatConnection` - all data associated with a single WebSocket connection
- `ClientChatMessage` - type of the incoming messages
- `ServerChatMessage` - type of the outgoing messages

## Connection Model

Connection-specific data is defined using a class annotated with `@Connection`.

````kotlin
@Connection
data class ChatConnection(
    @PathParameter val roomId: String
)
````

Values are mostly derived from the initial http request and are available for the entire lifetime of the connection.

### Supported Connection Bindings

- `@PathParameter` - injects url path parameter with the same or specified name
- `@QueryParameter` - injects url query parameter with the same or specified name
- `@HeaderParameter` - injects header value with the same or specified name
- `@CookieParameter` - injects cookie value with the same or specified name
- `@Principal` - injects the ktor authentication principal
- `@Call` - injects the underlying ktor call
- `@WebSocketSession` - injects the underlying ktor websocket session


## Incoming messages

Incoming messages are defined as normal serializable classes. This can be a single type or a sealed class hierarchy:

````kotlin
@Serializable
@JsonClassDiscriminator("_type")
sealed interface ClientChatMessage {

    @Serializable
    @SerialName("text")
    class Text(
        val timestamp: Long,
        val message: String
    ) : ClientChatMessage

    @Serializable
    @SerialName("file")
    class File(
        val timestamp: Long,
        val url: String
    ) : ClientChatMessage
}
````



## Outgoing messages

Outgoing messages are defined as normal serializable classes. This can be a single type or a sealed class hierarchy:

````kotlin
@Serializable
class ServerChatMessage(
    val timestamp: Long,
    val message: String
)
````



## Lifecycle Hooks

WebSocket handlers expose explicit lifecycle callbacks:

````kotlin
webSocket<...> {
    onOpen { context, connection ->
        // called after successful connection
    }

    onMessage { context, connection, message ->
        // called for each incoming message
    }

    onClose { context, connection ->
        // called before connection is closed
    }
}
````

- `onOpen` is always called first after a connection has been established
- `onMessage` is called on every incoming message
- `onClose` is always called just before a connection is closed, either by the server or client
- `context` provides access to further functionality, e.g. sending messages to a specific set of connections
- `connection` the fully initialized connection with the specified data
- `message` the incoming serialized message 


## Working with Connections

The websocket context provides access to all active connections for a specific websocket route.

### Sending Messages

Messages can be sent to any set of active connections:

````kotlin
context
    .connections()
    .filter { it.roomId == connection.roomId }
    .send(ServerChatMessage(now(), "Hello World"))
````


### Closing Connections

Connections can be closed explicitly:

````kotlin
context
    .connections()
    .close(CloseReason(CloseReason.Codes.NORMAL, "Ended Chat"))
````

Close reasons are forwarded to clients as defined by the WebSocket protocol.

### External WebSocket Context Access

A WebSocket route can expose its connection context outside the route handler. This allows other parts of the application (services, schedulers, event handlers, etc.) to interact with active WebSocket connections.

To access connections externally, the `WebSocketContext` must be created explicitly and passed into the route definition.

````kotlin
val chatContext = WebSocketContext.create<ChatConnection, ServerChatMessage>()

routing {
    webSocket<ChatConnection, ClientChatMessage, ServerChatMessage>(
        "chat/{roomId}",
        chatContext // pass the context to the route
    ) {
        onOpen { context, connection -> }
        onClose { context, connection -> }
        onMessage { context, connection, message -> }
    }
}

````

This binds the route lifecycle to the externally managed context. Once the context is shared, it can be used anywhere it is accessible:

````kotlin
chatContext
    .connections()
    .filter { it.roomId == connection.roomId }
    .send(ServerChatMessage(now(), "Hello World"))
````