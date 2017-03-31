package gg.uhc.migration.collection

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Maps
import java.util.*

/**
 * Stores items and retrieves which can be retrieved randomly calculated based on the weight of each item.
 * This collection is 100% not thread safe
 */
class RandomWeightedSet<T> : MutableSet<T> {
    var random = Random()

    private data class InternalKeyWeightPair(val internalKey: Int, val weight: Int)

    // Map of internal keys → elements
    private val map = Maps.newTreeMap<Int, T>()
    // Map of elements → internal internalKey + weight
    private val metadata = mutableMapOf<T, InternalKeyWeightPair>()

    // Safe way of checking highest internalKey in the treemap
    fun <V> TreeMap<Int, V>.safeLastKey() : Int = if (this.isEmpty()) 0 else this.lastKey()

    fun <K, V> TreeMap<K, V>.ceilingValue(key: K): V = this.ceilingEntry(key).value

    /**
     * Get a random element based on stored weights
     */
    fun get() : T {
        checkState(map.size != 0, "No items have been added to the collection yet")

        val index = random.nextInt(map.safeLastKey()) + 1
        return map.ceilingValue(index)
    }

    /**
     * Gets the weight for the given element or null if it is unknown
     */
    fun getWeight(element: T): Int? = metadata[element]?.weight

    /**
     * Add an element with the specified weight
     */
    fun add(element: T, weight: Int): Boolean {
        checkArgument(weight > 0, "Weight must be a positive integer (non zero)")

        if (map.containsValue(element))
            return false

        // Increment internalKey
        val nextKey = map.safeLastKey() + weight

        // add to maps
        map.put(nextKey, element)
        metadata.put(element, InternalKeyWeightPair(internalKey = nextKey, weight = weight))

        return true
    }

    /**
     * Adds an element with weight 1
     */
    override fun add(element: T): Boolean = add(element, 1)

    override fun clear() {
        this.map.clear()
        this.metadata.clear()
    }

    override val size: Int
        get() = map.size

    override fun contains(element: T): Boolean = map.containsValue(element)

    override fun containsAll(elements: Collection<T>): Boolean = map.values.containsAll(elements)

    override fun isEmpty(): Boolean = map.isEmpty()

    /**
     * Adds all the elements with weight 1
     */
    override fun addAll(elements: Collection<T>): Boolean = addAll(elements, elements.map { 1 })

    fun addAll(elements: Collection<T>, weights: Collection<Int>): Boolean {
        checkArgument(elements.size == weights.size, "Elements must be same size as weights")

        return elements
            .zip(weights)
            .map { add(element = it.first, weight = it.second) }
            .any { it }
    }

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        val wrapped = map.values.iterator()

        override fun next(): T = wrapped.next()

        override fun remove() = throw UnsupportedOperationException()

        override fun hasNext(): Boolean = wrapped.hasNext()
    }

    fun iteratorWithWeight(): MutableIterator<Pair<T, Int>> = object : MutableIterator<Pair<T, Int>> {
        val wrapped = map.values.iterator()

        override fun next(): Pair<T, Int> {
            val next = wrapped.next()

            return next to metadata[next]!!.weight
        }

        override fun remove() = throw UnsupportedOperationException()

        override fun hasNext(): Boolean = wrapped.hasNext()
    }

    override fun remove(element: T): Boolean {
        // Find the internalKey of the element to remove
        val internalKey = metadata[element]?.internalKey ?: return false

        // remove the element from both maps
        map.remove(internalKey)
        metadata.remove(element)

        // map of 'to remove' internalKey → element
        val tail = map
            .tailMap(internalKey)
            .toMap()

        val elements = tail.values

        // map of 'to remove' element → weight
        val backup = elements.map { it to metadata[it]!!.weight }.toMap()

        // Remove items from both maps
        tail.forEach {
            map.remove(it.key)
            metadata.remove(it.value)
        }

        // Readd remaining back in to the maps again
        addAll(elements = backup.keys, weights = backup.values)

        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean =
    // TODO better performance by not running per element?
        elements
            .map { remove(it) }
            .any { it }

    override fun retainAll(elements: Collection<T>): Boolean {
        // Take a copy of the values we need to remember
        val temp = map.values.intersect(elements)
        // Take a copy of the weights
        val weights = temp.map { metadata[it]!!.weight }

        // Don't run if nothing was removed
        if (temp.size == map.size) return false

        // Remove all entries
        clear()

        // Add all of the matching entries back
        addAll(temp, weights)

        return true
    }
}


