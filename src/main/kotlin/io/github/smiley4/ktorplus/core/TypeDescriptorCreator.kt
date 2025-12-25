package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptor
import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptorEntry
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Analyzes a given type and create a [TypeDescriptor] for it.
 */
class TypeDescriptorCreator(
    val typeAnalyzers: List<TypeAnalyzer<Annotation, TypeDescriptorEntry>>,
    val propertyAnalyzers: List<PropertyAnalyzer<Annotation, TypeDescriptorEntry>>,
) {

    /**
     * Analyzes a given [type] and creates a [TypeDescriptor] for it.
     */
    fun process(type: KType): TypeDescriptor {
        val classifier = type.classifier
        if (classifier is KClass<*>) {
            return process(classifier)
        }
        throw IllegalArgumentException("Type must be a class.")
    }

    /**
     * Analyzes a given [type] and creates a [TypeDescriptor] for it.
     */
    fun process(type: KClass<*>): TypeDescriptor {
        val typeDescriptorEntries = mutableListOf<TypeDescriptorEntry>()

        typeAnalyzers
            .find {
                type.annotations.any { annotation -> it.applicableTo(annotation) }
            }
            ?.let { analyzer ->
                val annotation = type.annotations.find { annotation -> analyzer.applicableTo(annotation) }
                    ?: throw IllegalArgumentException("Could not find required annotation.")
                analyzer to annotation
            }
            ?.also { (analyzer, annotation) ->
                typeDescriptorEntries.add(analyzer.process(type, annotation))
            }


        type.members.forEach { member ->
            propertyAnalyzers
                .find { member.annotations.any { annotation -> it.applicableTo(annotation) } }
                ?.let { analyzer ->
                    val annotation = member.annotations.find { annotation -> analyzer.applicableTo(annotation) }
                        ?: throw IllegalArgumentException("Could not find required annotation.")
                    analyzer to annotation
                }
                ?.also { (analyzer, annotation) ->
                    typeDescriptorEntries.add(analyzer.process(type, member, annotation))
                }
        }

        return TypeDescriptor(typeDescriptorEntries)
    }

}
