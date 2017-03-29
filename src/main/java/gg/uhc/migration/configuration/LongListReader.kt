package gg.uhc.migration.configuration

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toLongList(key: String): List<Long> {
    if (!this.isList(key))
        throw wrongType(key, "list of numbers", this.get(key))

    return this.getLongList(key)
}