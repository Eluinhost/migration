package gg.uhc.migration;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.ChatColor;

import java.util.Map;

public class PlayerStrings {

    protected final Map<String, String> map = Maps.newHashMap();

    public PlayerStrings setString(String name, String format) {
        this.map.put(name, ChatColor.translateAlternateColorCodes('&', format));
        return this;
    }

    public String format(String name, Object... args) {
        String message = map.get(name);

        if (message == null) {
            message = "MISSING MESSAGE KEY `" + name  + "`";
        }

        return String.format(message, args);
    }
}
