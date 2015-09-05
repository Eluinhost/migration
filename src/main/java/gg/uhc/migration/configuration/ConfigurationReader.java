package gg.uhc.migration.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public abstract class ConfigurationReader<T> {

    protected static final String MISSING_KEY = "Required configuration key missing `%s`";
    protected static final String WRONG_TYPE = "Expected key `%s` to be a '%s' but found: %s";

    public final T readFromSection(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.contains(key)) throw up(MISSING_KEY, key);

        return read(section, key);
    }

    protected abstract T read(ConfigurationSection section, String key) throws InvalidConfigurationException;

    protected final InvalidConfigurationException up(String message, Object... params) throws InvalidConfigurationException {
        return new InvalidConfigurationException(String.format(message, params));
    }
}
