package gg.uhc.migration.configuration

import com.google.common.collect.Range
import org.bukkit.configuration.ConfigurationSection

private val INCORRECT_RANGE_NOTATION = "Incorrect range notation for %s: %s"

fun ConfigurationSection.toRange(key: String): Range<Double> {
    if (!this.isString(key))
        throw wrongType(key, "String", this.get(key))

    val rangeString = this.getString(key)

    try {
        return rangeString.readRange()
    } catch (e: IllegalArgumentException) {
        throw generic(String.format(INCORRECT_RANGE_NOTATION, key, rangeString))
    }
}