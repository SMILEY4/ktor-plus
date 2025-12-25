@file:OptIn(ExperimentalSerializationApi::class)

package io.github.smiley4.ktorplus.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.WebSocketContext
import io.github.smiley4.ktorplus.data.Body
import io.github.smiley4.ktorplus.data.Connection
import io.github.smiley4.ktorplus.data.HttpStatusCode
import io.github.smiley4.ktorplus.data.PathParameter
import io.github.smiley4.ktorplus.data.Request
import io.github.smiley4.ktorplus.data.Response
import io.github.smiley4.ktorplus.webSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.CloseReason
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    val json = Json {
        prettyPrint = true
        isLenient = true
    }

    install(Authentication) {
        basic("user_auth") {
            validate { credentials ->
                if (credentials.name == "user" && credentials.password == "secret") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
//        provider("ws_auth") {
//            authenticate { context ->
//                val call = context.call
//                val token = call.request.queryParameters["token"]
//                if (token != null && token != "invalidtoken") {
//                    context.principal(UserPrincipal(token))
//                } else {
//                    context.challenge("TokenAuth", AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
//                        call.respond(UnauthorizedResponse())
//                        challenge.complete()
//                    }
//                }
//            }
//        }
    }

    install(WebSockets)

    install(ContentNegotiation) {
        json(json)
    }

    KtorPlusConfig.json = json

    install(OpenApi) {
        info {
            title = "Example"
            version = "0.1"
        }
        security {
            securityScheme("user_auth") {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            }
        }
        schemas {
            generator = SchemaGenerator.kotlinx(json)
        }
        examples {
            encoder(ExampleEncoder.kotlinx(json))
        }
    }

    val chatWsContext = WebSocketContext.create<ChatConnection, ServerChatMessage>()

    routing {
        webSocket<ChatConnection, ClientChatMessage, ServerChatMessage>("chat/{roomId}", chatWsContext) {
            onOpen { context, connection ->
                println("on open ${connection.roomId}")
            }
            onClose { context, connection ->
                println("on close ${connection.roomId}")
            }
            onMessage { context, connection, message ->
                when (message) {
                    is ClientChatMessage.Text -> println("on text ${connection.roomId}: ${message.timestamp} ${message.message}")
                    is ClientChatMessage.Emoji -> println("on emoji ${connection.roomId}: ${message.timestamp} ${message.emojiCode}")
                }
                context.connections().send(ServerChatMessage(117, "Hello back"))
                context.connections().close(CloseReason(CloseReason.Codes.NORMAL, "Ended Chat"))
            }
        }
    }

    chatWsContext.connections().send(ServerChatMessage(117, "Hello back"))



}


@Connection
private data class ChatConnection(
    @PathParameter
    val roomId: String,
)


@Request
private class LoginRequest(
    @Body val body: LoginData
)


@Serializable
@JsonClassDiscriminator("_type")
sealed interface ClientChatMessage {

    @SerialName("text")
    @Serializable
    class Text(
        val timestamp: Long,
        val message: String
    ) : ClientChatMessage


    @SerialName("emoji")
    @Serializable
    class Emoji(
        val timestamp: Long,
        val emojiCode: Int
    ) : ClientChatMessage

}


@Serializable
class ServerChatMessage(
    val timestamp: Long,
    val message: String
)


private sealed class LoginResponse {

    @Response(HttpStatusCode.OK, "The user successfully logged in.")
    class Success(
        @Body val body: AuthDataDto,
    ) : LoginResponse()


    @Response(HttpStatusCode.UNAUTHORIZED, "Invalid credentials")
    class Unauthorized(
        @Body val body: String = "Invalid credentials"
    ) : LoginResponse()


    @Response(HttpStatusCode.INTERNAL_SERVER_ERROR, "An internal error occurred.")
    class InternalError(
        @Body val body: String = "An internal error occurred"
    ) : LoginResponse()

}


@Serializable
private data class LoginData(
    val username: String,
    val password: String
)


@Serializable
private data class AuthDataDto(
    val userId: String,
    val authToken: String,
)
