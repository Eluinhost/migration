package gg.uhc.migration.selection

import gg.uhc.migration.Area

import java.util.UUID

class GlobalAreaSelection : AreaSelection() {
    private var chosenArea: Area? = null

    override fun getForUUID(uuid: UUID): Area {
        if (chosenArea == null) {
            randomize()
        }

        return chosenArea!!
    }

    override fun randomize() {
        chosenArea = this.potentialAreas.get()
    }
}
