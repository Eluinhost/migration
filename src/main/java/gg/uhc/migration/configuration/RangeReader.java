package gg.uhc.migration.configuration;

import com.google.common.collect.Range;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class RangeReader extends ConfigurationReader<Range<Double>> {

    protected static final String INCORRECT_RANGE_NOTATION = "Incorrect range notation for %s: %s";

    protected final RangeParser rangeParser;

    public RangeReader(RangeParser rangeParser) {
        this.rangeParser = rangeParser;
    }

    @Override
    protected Range<Double> read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isString(key)) throw up(WRONG_TYPE, key, "String", section.get(key));

        String rangeString = section.getString(key);

        try {
            return rangeParser.readRange(rangeString);
        } catch (IllegalArgumentException e) {
            throw up(INCORRECT_RANGE_NOTATION, key, rangeString);
        }
    }


}
