package io.github.smiley4.ktorplus.data

/**
 * Marks a class as a definition for a request.
 */
@Target(
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Request
