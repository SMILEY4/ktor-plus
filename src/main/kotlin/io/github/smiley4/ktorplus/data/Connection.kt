package io.github.smiley4.ktorplus.data

/**
 * Marks a class as a definition for a websocket connection.
 */
@Target(
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Connection
