package gg.uhc.migration.selection;

import gg.uhc.migration.Area;
import gg.uhc.migration.collection.RandomCollection;

import java.util.List;
import java.util.UUID;

public abstract class AreaSelection {

    protected final RandomCollection<Area> potentialAreas = new RandomCollection<>();

    public void setPotentialAreas(List<Area> areas) {
        potentialAreas.clear();

        for (Area area : areas) {
            this.potentialAreas.add(area, area.getWeight());
        }
    }

    /**
     * Get the current area for the given player, if they don't have one yet it is assigned
     *
     * @param uuid the uuid to check
     *
     * @return their area
     */
    public abstract Area getForUUID(UUID uuid);

    /**
     * Selects new area/s
     */
    public abstract void randomize();
}
