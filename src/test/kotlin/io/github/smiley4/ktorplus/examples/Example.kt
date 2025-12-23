package io.github.smiley4.ktorplus.examples

import io.github.smiley4.ktorplus.data.HttpStatusCode
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorplus.KtorPlusConfig
import io.github.smiley4.ktorplus.data.Body
import io.github.smiley4.ktorplus.data.Request
import io.github.smiley4.ktorplus.data.Response
import io.github.smiley4.ktorplus.post
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
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

    install(ContentNegotiation) {
        json(json)
    }

    install(OpenApi) {
        info {
            title = "Group Vacation Planner API"
            version = "indev"
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
            } catch (e:Exception){
                e.printStackTrace()
                LoginResponse.InternalError()
            }
        }
    }

}


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
