package gg.uhc.migration.configuration;

import com.google.common.collect.Range;
import gg.uhc.migration.Area;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class AreaReader extends ConfigurationReader<Area> {

    protected final RangeReader rangeReader;

    public AreaReader(RangeReader rangeReader) {
        this.rangeReader = rangeReader;
    }

    @Override
    protected Area read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection areaSection = section.getConfigurationSection(key);

        Range<Double> xRange = rangeReader.read(areaSection, "x");
        Range<Double> zRange = rangeReader.read(areaSection, "z");

        if (!areaSection.isString("announce")) throw up(WRONG_TYPE, key, "String", areaSection.get("announce"));

        // optional weight
        int weight;
        if (areaSection.contains("weight")) {
            if (!areaSection.isInt("weight")) throw up(WRONG_TYPE, key + ".weight", ">= 0 integer", areaSection.get("weight"));

            weight = areaSection.getInt("weight");

            if (weight < 0) throw up(WRONG_TYPE, key + ".weight", ">= 0 integer", weight);
        } else {
            weight = 1;
        }

        return new Area(xRange, zRange, areaSection.getString("announce"), weight);
    }
}
