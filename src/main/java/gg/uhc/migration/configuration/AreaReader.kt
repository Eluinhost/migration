package gg.uhc.migration.configuration

import gg.uhc.migration.Area
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toArea(key: String): Area {
    if (!this.isConfigurationSection(key))
        throw wrongType(key, "Section", this.get(key))

    val areaSection = this.getConfigurationSection(key)

    val xRange = areaSection.toRange("x")
    val zRange = areaSection.toRange("z")

    if (!areaSection.isString("announce"))
        throw wrongType(key, "String", areaSection.get("announce"))

    // optional weight
    val weight = when {
        areaSection.contains("weight") -> areaSection.toPositiveInteger("weight", allowZero = true)
        else -> 1
    }

    return Area(xRange, zRange, areaSection.getString("announce"), weight)
}