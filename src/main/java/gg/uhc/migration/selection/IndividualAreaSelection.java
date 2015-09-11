package gg.uhc.migration.selection;

import com.google.common.collect.Maps;
import gg.uhc.migration.Area;

import java.util.Map;
import java.util.UUID;

public class IndividualAreaSelection extends AreaSelection {

    protected final Map<UUID, Area> playerAreas = Maps.newHashMap();

    @Override
    public Area getForUUID(UUID uuid) {
        Area area = playerAreas.get(uuid);

        if (area == null) {
            area = potentialAreas.random();
            playerAreas.put(uuid, area);
        }

        return area;
    }

    @Override
    public void randomize() {
        for (UUID player : playerAreas.keySet()) {
            playerAreas.put(player, potentialAreas.random());
        }
    }
}
