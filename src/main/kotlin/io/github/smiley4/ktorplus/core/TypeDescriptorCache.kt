package io.github.smiley4.ktorplus.core

import io.github.smiley4.ktorplus.typedescriptor.TypeDescriptor
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Stores already created [TypeDescriptor]. Uses [creator] in case requested type it not yet known.
 */
class TypeDescriptorCache(private val creator: TypeDescriptorCreator) {

    private val typeDescriptors = mutableMapOf<KClass<*>, TypeDescriptor>()


    /**
     * @return the [TypeDescriptor] for the given type.
     */
    fun get(type: KType): TypeDescriptor {
        val classifier = type.classifier
        if (classifier is KClass<*>) {
            return get(classifier)
        }
        throw IllegalArgumentException("Type must be a class.")
    }


    /**
     * @return the [TypeDescriptor] for the given class.
     */
    fun get(type: KClass<*>): TypeDescriptor = typeDescriptors.getOrPut(type) {
        creator.process(type)
    }

}
