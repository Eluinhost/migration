package gg.uhc.migration.collection

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkState
import com.google.common.collect.Maps
import java.util.*

data class WeightedEntry<out T>(val weight: Double, val item: T)

/**
 * Stores items and retrieves which can be retrieved randomly calculated based on the weight of each item.
 * This collection is 100% not thread safe
 */
class RandomCollection<T> : MutableCollection<WeightedEntry<T>> {
    companion object {
        private val random = Random()
    }

    private val map = Maps.newTreeMap<Double, WeightedEntry<T>>()
    private val inverse = mutableMapOf<WeightedEntry<T>, Double>()

    private val lastKey: Double
        get() = if (map.size > 0) map.lastKey() else 0.0

    /**
     * Automatic unwrap version of #getWithWeight
     */
    fun get() : T = getWithWeight().item

    /**
     * Wrapping version of add(WeightedEntry)
     */
    fun add(element: T, weight: Double) = add(WeightedEntry(weight, element))

    /**
     * Returns a random item from the collection
     */
    fun getWithWeight(): WeightedEntry<T> {
        checkState(map.size != 0, "No items have been added to the collection yet")

        val index = random.nextDouble() * lastKey
        return map.ceilingEntry(index).value
    }

    override fun add(element: WeightedEntry<T>): Boolean {
        checkArgument(element.weight > 0, "Weight must be a positive integer")

        // Check if we already have this
        if (inverse.containsKey(element))
            return false

        // Increment key
        val nextKey = lastKey + element.weight

        // add to maps
        map.put(nextKey, element)
        inverse.put(element, nextKey)

        return true
    }

    override fun clear() {
        this.map.clear()
        this.inverse.clear()
    }

    override val size: Int
        get() = map.size

    override fun contains(element: WeightedEntry<T>): Boolean = inverse.containsKey(element)

    override fun containsAll(elements: Collection<WeightedEntry<T>>): Boolean = inverse.keys.containsAll(elements)

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun addAll(elements: Collection<WeightedEntry<T>>): Boolean {
        if (elements.isEmpty())
            return false

        elements.forEach { add(it) }

        return true
    }

    override fun iterator(): MutableIterator<WeightedEntry<T>> = throw UnsupportedOperationException()

    fun removeElement(element: T): Boolean {
        val found = map.entries.firstOrNull { it.value.item == element }?.value ?: return false

        return remove(found)
    }

    override fun remove(element: WeightedEntry<T>): Boolean {
        if (!inverse.containsKey(element))
            return false

        val key = inverse[element]

        // Grab a copy of all keys >= key
        val tail = map.tailMap(key, true).toMutableMap()

        // Remove from both maps
        tail.forEach {
            inverse.remove(it.value)
            map.remove(it.key)
        }

        // Remove the element from the tail
        tail.remove(key)

        // Readd remaining the maps again in the same order
        addAll(tail.values)

        return true
    }

    override fun removeAll(elements: Collection<WeightedEntry<T>>): Boolean =
        elements.map { remove(it) }.any { it }

    override fun retainAll(elements: Collection<WeightedEntry<T>>): Boolean {
        // Take a copy of the values we need to remember
        val temp = map.values.intersect(elements)

        // Don't run if nothing was removed
        if (temp.size == map.size) return false

        // Remove all entries
        clear()

        // Add all of the matching entries back in in the original order
        addAll(temp)

        return true
    }
}


