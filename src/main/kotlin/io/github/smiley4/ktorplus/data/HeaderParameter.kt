package io.github.smiley4.ktorplus.data

/**
 * Injects (for requests) or sets (for responses) a header.
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class HeaderParameter(
    val name: String = "",
    val description: String = "",
)
