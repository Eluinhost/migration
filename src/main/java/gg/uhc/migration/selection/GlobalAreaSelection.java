package gg.uhc.migration.selection;

import gg.uhc.migration.Area;

import java.util.UUID;

public class GlobalAreaSelection extends AreaSelection {

    protected Area chosenArea = null;

    @Override
    public Area getForUUID(UUID uuid) {
        if (chosenArea == null) {
            randomize();
        }

        return chosenArea;
    }

    @Override
    public void randomize() {
        chosenArea = this.potentialAreas.random();
    }
}
