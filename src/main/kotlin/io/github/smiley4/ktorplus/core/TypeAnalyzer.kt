package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Analyzes a request or response type with a specific annotation and
 * creates a piece of information as a [TypeDescriptorEntry].
 * [TAnnotation] the type of the annotation.
 * [TTypeDescriptorEntry] the type of the resulting [TypeDescriptorEntry].
 */
interface TypeAnalyzer<TAnnotation : Annotation, TTypeDescriptorEntry : TypeDescriptorEntry> {

    /**
     * Whether this type analyzer is applicable to the given annotation
     */
    fun applicableTo(annotation: Annotation) = annotation.annotationClass == getAnnotationType().classifier

    /**
     * @return the type of the annotation the type must have. MUST MATCH [TAnnotation]!
     */
    fun getAnnotationType(): KType


    /**
     * @param type the annotated class
     * @param annotation the annotation
     * @return a piece of information as a [TypeDescriptorEntry].
     */
    fun process(type: KClass<*>, annotation: TAnnotation): TTypeDescriptorEntry
}
