package gg.uhc.migration.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class IntegerReader extends ConfigurationReader<Integer> {
    @Override
    protected Integer read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isInt(key)) throw up(WRONG_TYPE, key, "Integer", section.get(key));

        return section.getInt(key);
    }
}
