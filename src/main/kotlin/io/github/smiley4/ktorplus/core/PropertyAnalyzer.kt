package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.data.TypeDescriptorEntry
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Analyzes a single property of a request or response type with a specific annotation and
 * creates a piece of information as a [TypeDescriptorEntry].
 * [TAnnotation] the type of the annotation.
 * [TTypeDescriptorEntry] the type of the resulting [TypeDescriptorEntry].
 */
interface PropertyAnalyzer<TAnnotation : Annotation, TTypeDescriptorEntry : TypeDescriptorEntry> {

    /**
     * Whether this property analyzer is applicable to the given annotation
     */
    fun applicableTo(annotation: Annotation) = annotation.annotationClass == getAnnotationType().classifier


    /**
     * @return the type of the annotation the property must have. MUST MATCH [TAnnotation]!
     */
    fun getAnnotationType(): KType


    /**
     * @param type the parent class of the property
     * @param property the annotated property
     * @param annotation the annotation
     * @return a piece of information as a [TypeDescriptorEntry].
     */
    fun process(type: KClass<*>, property: KCallable<*>, annotation: TAnnotation): TTypeDescriptorEntry
}
