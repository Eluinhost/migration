package gg.uhc.migration.configuration

import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toPositiveInteger(key: String, allowZero: Boolean): Int {
    val read = this.toInteger(key)

    if (read < 0)
        throw wrongType(key, if (allowZero) ">= 0" else "> 0", read)

    if (read > 0)
        return read

    if (!allowZero)
        throw wrongType(key, "> 0", 0)

    return read
}