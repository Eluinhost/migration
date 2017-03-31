package gg.uhc.migration.configuration

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toInteger(key: String): Int {
    if (!this.isInt(key))
        throw wrongType(key, "Integer", this.get(key))

    return this.getInt(key)
}