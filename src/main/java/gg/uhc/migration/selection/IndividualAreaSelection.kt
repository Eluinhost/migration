package gg.uhc.migration.selection

import gg.uhc.migration.Area
import java.util.*

class IndividualAreaSelection : AreaSelection() {
    private var playerAreas: MutableMap<UUID, Area> = mutableMapOf()

    override fun getForUUID(uuid: UUID): Area = playerAreas.getOrPut(uuid) { potentialAreas.get() }

    override fun randomize() {
        playerAreas = playerAreas.mapValues { potentialAreas.get() }.toMutableMap()
    }
}
