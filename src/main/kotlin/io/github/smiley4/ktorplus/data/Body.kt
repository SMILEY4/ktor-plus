package io.github.smiley4.ktorplus.data

/**
 * Injects the request body for requests. Defines the output response body for responses.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Body(
    val description: String = "",
)
