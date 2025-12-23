package io.github.smiley4.ktorplus.data

/**
 * Injects a path parameter from the url (requests only).
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class PathParameter(
    val name: String = "",
    val description: String = "",
)
