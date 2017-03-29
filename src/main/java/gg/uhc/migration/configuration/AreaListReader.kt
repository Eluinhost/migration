package gg.uhc.migration.configuration

import gg.uhc.migration.Area
import org.bukkit.configuration.ConfigurationSection

private val INVALID_LENGTH = "Area list requires at least 1 non-zero weight ares to be defined"

fun ConfigurationSection.toAreaList(key: String): List<Area> {
    if (!this.isConfigurationSection(key))
        throw wrongType(key, "Section", this.get(key))

    val areaListSection = this.getConfigurationSection(key)

    val keys = areaListSection.getKeys(false)

    val areas = keys
        .map { areaListSection.toArea(it) }
        .filter { it.weight > 0 } // skip weight 0 areas

    if (areas.isEmpty())
        throw generic(INVALID_LENGTH)

    return areas
}