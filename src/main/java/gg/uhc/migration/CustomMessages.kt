package gg.uhc.migration

import net.md_5.bungee.api.ChatColor

class CustomMessages {
    private val map: MutableMap<String, String> = mutableMapOf()

    operator fun set(name: String, format: String): Unit {
        this.map.put(name, ChatColor.translateAlternateColorCodes('&', format))
    }

    operator fun get(name: String, vararg args: Any) =
        String.format(map.getOrElse(name, { "MISSING MESSAGE KEY `$name`" }), *args)
}
