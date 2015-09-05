package gg.uhc.migration.configuration;

import com.google.common.collect.Lists;
import gg.uhc.migration.Area;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Set;

public class AreaListReader extends ConfigurationReader<List<Area>> {

    protected final AreaReader areaReader;

    protected static final String INVALID_LENGTH = "Area list requires at least 1 non-zero weight ares to be defined";

    public AreaListReader(AreaReader areaReader) {
        this.areaReader = areaReader;
    }

    @Override
    protected List<Area> read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isConfigurationSection(key)) throw up(WRONG_TYPE, key, "Section", section.get(key));

        ConfigurationSection areaListSection = section.getConfigurationSection(key);

        Set<String> keys = areaListSection.getKeys(false);

        List<Area> areas = Lists.newArrayList();
        for (String areaKey : keys) {
            Area area = areaReader.read(areaListSection, areaKey);

            // skip weight 0 areas
            if (area.getWeight() > 0) {
                areas.add(area);
            }
        }

        if (areas.size() == 0) throw up(INVALID_LENGTH);

        return areas;
    }
}
