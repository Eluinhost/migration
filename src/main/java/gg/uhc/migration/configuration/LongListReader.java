package gg.uhc.migration.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;

public class LongListReader extends ConfigurationReader<List<Long>> {
    @Override
    protected List<Long> read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isList(key)) throw up(WRONG_TYPE, key, "list of numbers", section.get(key));

        return section.getLongList(key);
    }
}
