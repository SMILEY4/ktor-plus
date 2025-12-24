package io.github.smiley4.ktorplus.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.data.Body
import io.github.smiley4.ktorplus.data.Connection
import io.github.smiley4.ktorplus.data.HttpStatusCode
import io.github.smiley4.ktorplus.data.PathParameter
import io.github.smiley4.ktorplus.data.Request
import io.github.smiley4.ktorplus.data.Response
import io.github.smiley4.ktorplus.post
import io.github.smiley4.ktorplus.webSocket
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    install(OpenApi) {
        info {
            title = "Group Vacation Planner API"
            version = "indev"
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

    KtorPlusConfig.json = json

    routing {
        route("api.json") {
            openApi()
        }
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("redoc") {
            redoc("/api.json")
        }

        post<LoginRequest, LoginResponse>("/login", {
            description = "Allows the user to log in with personal credentials. Provides token used for further authentication."
        }) { request ->
            try {
                if (request.body.username == "myusername" && request.body.password == "mysecret") {
                    LoginResponse.Success(AuthDataDto("myuserid", "mytoken"))
                } else {
                    LoginResponse.Unauthorized()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LoginResponse.InternalError()
            }
        }

        webSocket<ChatConnection>("chat/{roomId}") {
            onOpen { connection ->
                println("on open ${connection.roomId}")
            }
            onClose { connection ->
                println("on close ${connection.roomId}")
            }
            onMessage { connection, message ->
                println("on message ${connection.roomId}: $message")
            }
        }
    }

}


@Connection
private class ChatConnection(
    @PathParameter
    val roomId: String
)


@Request
private class LoginRequest(
    @Body val body: LoginData
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
