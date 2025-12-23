package io.github.smiley4.ktorplus.data

/**
 * Injects a query parameter from the url (requests only).
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueryParameter(
    val name: String = "",
    val description: String = "",
)
