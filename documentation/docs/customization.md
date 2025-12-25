# Customization

## Custom Request Properties

**1. Create a new annotation for fields and properties**

This is for identifying properties to inject the value into.

````kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserId
````

**2. Create a new descriptor class**

This class is used for storing extracted information about the request class.

````kotlin
class UserIdDescriptor(
    val property: KCallable<*>,
) : TypeDescriptorEntry
````

**3. Create a new analyzer**

This analyzer looks at properties annotated with the specified annotation and returns a specified descriptor with
information for later use.

````kotlin
class UserIdAnalyzer : PropertyAnalyzer<UserId, TypeDescriptorEntry> {

    override fun getAnnotationType() = typeOf<UserId>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: UserId) = UserIdDescriptor(
        property = property,
    )
}
````

The analyzer also has to be registered at the ktor plus configuration. Matching analyzers with lower index in the list
have higher priority over matching analyzers further back.

````kotlin
KtorPlusConfig.propertyAnalyzers.add(0, UserIdAnalyzer())
````

**4. Create a handler for the property**

This class handles the matching descriptor, extracts the actual value from the http request and returns collected data.
The returned keys must match the property names in the class.

````kotlin
class UserIdRequestPropertyHandler : RequestPropertyHandler<UserIdDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is UserIdDescriptor

    override suspend fun handle(descriptor: UserIdDescriptor, call: RoutingCall): Map<String, Any?> {
        val principal = call.authentication.principal<JWTPrincipal>()
            ?: throw IllegalArgumentException("Missing jwt principal for user id.")
        return mapOf(
            descriptor.property.name to principal.getUserId(),
        )
    }

}
````

The handler also has to be registered at the ktor plus configuration. Matching handlers with lower index in the list
have higher priority over matching handlers further back.

````kotlin
KtorPlusConfig.requestHandlers.add(0, UserIdResponsePropertyHandler(KtorPlusConfig.encoders))
````

## Custom Response Properties

**1. Create a new annotation for fields and properties**

This is for identifying properties to inject the value into.

````kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserId
````

**2. Create a new descriptor class**

This class is used for storing extracted information about the response class.

````kotlin
class UserIdDescriptor(
    val property: KCallable<*>,
) : TypeDescriptorEntry
````

**3. Create a new analyzer**

This analyzer looks at properties annotated with the specified annotation and returns a specified descriptor with
information for later use.

````kotlin
class UserIdAnalyzer : PropertyAnalyzer<UserId, TypeDescriptorEntry> {

    override fun getAnnotationType() = typeOf<UserId>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: UserId) = UserIdDescriptor(
        property = property,
    )
}
````

The analyzer also has to be registered at the ktor plus configuration. Matching analyzers with lower index in the list
have higher priority over matching analyzers further back.

````kotlin
KtorPlusConfig.propertyAnalyzers.add(0, UserIdAnalyzer())
````

**4. Create a handler for the property**

This class handles the matching descriptor and sets attributes of the actual http response.

````kotlin
class UserIdResponsePropertyHandler(
    private val encoders: () -> List<ParameterEncoder<*>>
) : ResponsePropertyHandler<UserIdDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is UserIdDescriptor

    override fun handle(descriptor: HeaderParameterDescriptor, response: Any, call: RoutingCall): HandledResponseData? {
        // find an encoder for the required type, ...
        val encoder = encoders()
            .firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException("No encoder found for type ${descriptor.property.returnType}")
        // ... encode the value and ...
        encoder.encodeUnsafe(descriptor.property.call(response))?.also {
            // ... append the value as a header to the response
            call.response.headers.append(descriptor.name, it)
        }
        // return nothing
        return null
    }

}
````

To set the response body or status code, return a `HandledResponseData` instead of null.

The handler also has to be registered at the ktor plus configuration. Matching handlers with lower index in the list
have higher priority over matching handlers further back.

````kotlin
KtorPlusConfig.responseHandlers.add(0, UserIdResponsePropertyHandler(KtorPlusConfig.encoders))
````

## Custom WebSocket Connection Properties

**1. Create a new annotation for fields and properties**

This is for identifying properties to inject the value into.

````kotlin
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserId
````

**2. Create a new descriptor class**

This class is used for storing extracted information about the connection class.

````kotlin
class UserIdDescriptor(
    val property: KCallable<*>,
) : TypeDescriptorEntry
````

**3. Create a new analyzer**

This analyzer looks at properties annotated with the specified annotation and returns a specified descriptor with
information for later use.

````kotlin
class UserIdAnalyzer : PropertyAnalyzer<UserId, TypeDescriptorEntry> {

    override fun getAnnotationType() = typeOf<UserId>()

    override fun process(type: KClass<*>, property: KCallable<*>, annotation: UserId) = UserIdDescriptor(
        property = property,
    )
}
````

The analyzer also has to be registered at the ktor plus configuration. Matching analyzers with lower index in the list
have higher priority over matching analyzers further back.

````kotlin
KtorPlusConfig.propertyAnalyzers.add(0, UserIdAnalyzer())
````

**4. Create a handler for the property**

This class handles the matching descriptor, extracts the actual value from the http request or websocket session and
returns collected data.
The returned keys must match the property names in the class.

````kotlin
class UserIdWebSocketConnectionPropertyHandler(
    private val decoders: () -> List<ParameterDecoder<*>>
) : WebSocketConnectionPropertyHandler<UserIdDescriptor> {

    override fun appliesTo(descriptor: TypeDescriptorEntry) = descriptor is UserIdDescriptor

    override suspend fun handle(
        descriptor: HeaderParameterDescriptor,
        call: ApplicationCall,
        session: WebSocketSession
    ): Map<String, Any?> {
        val rawValue = // ... get value from call or session
        val value = decode(rawValue, descriptor)
        return mapOf(descriptor.property.name to value)
    }

    fun decode(rawValue: String?, descriptor: HeaderParameterDescriptor): Any? {
        val decoder = decoders().firstOrNull { it.canHandle(descriptor.property.returnType) }
            ?: throw IllegalStateException("No decoder found for type ${descriptor.property.returnType}")
        return decoder.decode(rawValue, descriptor.property.returnType, KtorPlusConfig.json)
    }

}

````

````kotlin
KtorPlusConfig.connectionHandlers.add(0, UserIdWebSocketConnectionPropertyHandler(KtorPlusConfig.encoders))
````

## Custom Types

Values for parameters (e.g. headers, url parameters, cookies, etc) are usually encoded using a simple "to string"
function. For more complex types or types that require special handling, custom type encoders/decoders can be added.

````kotlin
@JvmInline
value class UserId(val value: UUID)
````

**Create a custom encoder**

````kotlin
class UserIdEncoder : ParameterEncoder<UserId> {
    override fun canHandle(type: KType) = type == UserId::class
    override fun encode(value: UserId?): String? = value?.value?.toString()
}
````

The encoder has to be registered at the ktor plus configuration:

````kotlin
KtorPlusConfig.encoders.add(0, UserIdEncoder())
````

**Create a custom decoder**

````kotlin
class UserIdDecoder : ParameterDecoder<UserId> {
    override fun canHandle(type: KType) = type == UserId::class
    override fun decode(value: String?, type: KType, json: Json) = value?.let { UserId(UUID.fromString(it)) }
}
````

The decoder has to be registered at the ktor plus configuration:

````kotlin
KtorPlusConfig.decoders.add(0, UserIdDecoder())
````