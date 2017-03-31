package gg.uhc.migration.configuration

import org.bukkit.configuration.InvalidConfigurationException

fun wrongType(key: String, expected: String, found: Any) =
    generic(String.format("Expected key `%s` to be a '%s' but found: %s", key, expected, found))

fun generic(message: String) =
    InvalidConfigurationException(message)