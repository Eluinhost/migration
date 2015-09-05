package gg.uhc.migration.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class PositiveIntegerReader extends IntegerReader {

    protected final boolean allowZero;

    public PositiveIntegerReader(boolean allowZero) {
        this.allowZero = allowZero;
    }

    @Override
    protected Integer read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        int integer = super.read(section, key);

        if (allowZero ? integer < 0 : integer <= 0) {
            throw up(WRONG_TYPE, key, allowZero ? ">= 0" : "> 0", integer);
        }

        return integer;
    }
}
