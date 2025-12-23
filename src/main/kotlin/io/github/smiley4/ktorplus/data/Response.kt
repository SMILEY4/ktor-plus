package io.github.smiley4.ktorplus.data

/**
 * Marks a class as a definition for a possible response.
 */
@Target(
    AnnotationTarget.CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Response(
    val statusCode: Int,
    val description: String = "",
)
