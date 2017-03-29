package gg.uhc.migration.configuration

import gg.uhc.migration.CustomMessages
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toCustomMessages(key: String): CustomMessages {
    if (!this.isConfigurationSection(key))
        throw wrongType(key, "Section", this.get(key))

    val messageSection = this.getConfigurationSection(key)

    val keys = messageSection.getKeys(false)

    val strings = CustomMessages()

    for (k in keys) {
        if (!messageSection.isString(k))
            throw wrongType(key + "." + k, "String", messageSection.get(k))

        strings[k] = messageSection.getString(k)
    }

    return strings
}