package io.github.smiley4.ktorplus.data

/**
 * Injects the current principal (requests only).
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Principal
