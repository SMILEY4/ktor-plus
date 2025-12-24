package io.github.smiley4.ktorplus.data

/**
 * Injects the ktor websocket session (connections only).
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class WebSocketSession
