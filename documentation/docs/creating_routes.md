# Creating Routes

Ktor-Plus provides a thin, type-safe(ish), declarative layer on top of Ktor to define HTTP routes.

Request data, responses, status codes are all expressed through Kotlin types and annotations.

## Defining a Route

Routes are defined using HTTP method functions with request and response types:

````kotlin
put<EditObjectRequest, EditObjectResponse>("object/{objectId}") { request ->
    // handle request...
}
````

- `EditObjectRequest` defines all incoming data (path parameter, cookies, body, ...)
- `EditObjectResponse` defines all possible outgoing responses, their status code and additional info (cookies,
  headers, ...)
- The handler receives an instance of `EditObjectRequest` with all specified values and returns a `EditObjectResponse`.

## Request Model

Requests are defined as Kotlin classes annotated with `@Request`.

````kotlin
@Request
class EditObjectRequest(
    @PathParameter val objectId: String,
    @Body val body: ObjectData
)
````

All values are automatically deserialized and validated before entering the handler. All values must be serializable by
kotlinx-serialization.

### Supported Request Bindings

- `@PathParameter` - injects url path parameter with the same or specified name
- `@QueryParameter` - injects url query parameter with the same or specified name
- `@HeaderParameter` - injects header value with the same or specified name
- `@CookieParameter` - injects cookie value with the same or specified name
- `@Body` - injects request body
- `@Principal` - injects the ktor authentication principal
- `@Call` - injects the underlying ktor call. Property type must be a ktor RoutingCall.

### Custom Request Bindings

See [Customization](./customization.md) for more information.

## Response Model

Possible responses are usually defined as a sealed class hierarchy. Each response variant is annotated with `@Response`.

````kotlin
sealed class EditObjectResponse {

    @Response(HttpStatusCode.OK)
    class Success(
        @Body val body: ObjectData
    ) : EditObjectResponse()


    @Response(HttpStatusCode.NOT_FOUND)
    class NotFound() : EditObjectResponse()

}
````

Each response has a fixed HTTP status code and optional additional data to return with the response. The handler must
return one of the declared responses.

### Supported Response Bindings

- `@Body` - the response body.
- `@CookieParameter` - sets a cookie with the same or specified name. Type can either be a ktor Cookie, a ktor-plus
  cookie or any value that will be automatically converted to a cookie.
- `@HeaderParameter` - sets a header with the same or specified name

### Custom Response Bindings

See [Customization](./customization.md) for more information.

## Handler Logic

Route handlers receive a fully typed request object and return a typed response:

````kotlin
put<EditObjectRequest, EditObjectResponse>("object/{objectId}") { request ->
    val objectId = request.objectId
    val updateData = request.body
    val updatedObject = service.update(objectId, updateData)
    return EditObjectResponse.Success(updatedObject)
}
````