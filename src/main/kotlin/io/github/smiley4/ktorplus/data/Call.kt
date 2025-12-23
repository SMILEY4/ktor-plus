package io.github.smiley4.ktorplus.data

/**
 * Injects the ktor call (requests only).
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Call
