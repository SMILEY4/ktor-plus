package io.github.smiley4.ktorplus.typedescriptor

/**
 * Describes a request or response type/definition.
 */
data class TypeDescriptor(
    val entries: List<TypeDescriptorEntry>
)
