package gg.uhc.migration.configuration;

import gg.uhc.migration.PlayerStrings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.Set;

public class PlayerStringsReader extends ConfigurationReader<PlayerStrings> {
    @Override
    protected PlayerStrings read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection messageSection = section.getConfigurationSection(key);

        Set<String> keys = messageSection.getKeys(false);

        PlayerStrings strings = new PlayerStrings();

        for (String k : keys) {
            if (!messageSection.isString(k)) throw up(WRONG_TYPE, key + "." + k, "String", messageSection.get(k));

            strings.setString(k, messageSection.getString(k));
        }

        return strings;
    }
}
