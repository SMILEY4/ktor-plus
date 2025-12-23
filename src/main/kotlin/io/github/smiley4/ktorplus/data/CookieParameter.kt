package io.github.smiley4.ktorplus.data

/**
 * Injects (for requests) or sets (for responses) a cookie.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class CookieParameter(
    val name: String = "",
    val description: String = "",
)
