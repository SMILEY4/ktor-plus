# Install and Configure Ktor Plus

## Configure Required Plugins

### Content Negotiation

Kotlinx-serialization json is required.

````kotlin
val json = Json {
    //...
}

install(ContentNegotiation) {
    json(json)
}
````

### Ktor-Plus

````kotlin
KtorPlusConfig.json = json
````

Using the same json instance as for content negotiation is recommended.

### Websockets (optional)

````kotlin
install(WebSockets) {
    //...
}
````

### OpenAPI (optional)

````kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx(json)
    }
    examples {
        encoder(ExampleEncoder.kotlinx(json))
    }
    //...
}
````

Kotlinx-serialization for schema generation and example encoding is required. Using the same json instance as for
content negotiation is recommended.
