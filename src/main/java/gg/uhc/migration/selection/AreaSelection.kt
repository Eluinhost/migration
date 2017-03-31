package gg.uhc.migration.selection

import gg.uhc.migration.Area
import gg.uhc.migration.collection.RandomWeightedSet
import java.util.*

abstract class AreaSelection {
    protected val potentialAreas = RandomWeightedSet<Area>()

    fun setPotentialAreas(areas: Collection<Area>) {
        potentialAreas.clear()
        areas.forEach {
            this.potentialAreas.add(it, it.weight)
        }
    }

    /**
     * Get the current area for the given player, if they don't have one yet it is assigned

     * @param uuid the uuid to check
     * *
     * *
     * @return their area
     */
    abstract fun getForUUID(uuid: UUID): Area

    /**
     * Selects new area/s
     */
    abstract fun randomize()
}
