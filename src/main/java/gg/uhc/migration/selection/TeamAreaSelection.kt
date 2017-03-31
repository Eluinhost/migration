package gg.uhc.migration.selection

import gg.uhc.migration.Area
import org.bukkit.Bukkit
import java.util.*

class TeamAreaSelection : AreaSelection() {
    private var teamAssigns: MutableMap<String?, Area> = mutableMapOf()

    override fun getForUUID(uuid: UUID): Area =
        teamAssigns
            .getOrPut(
                Bukkit
                    .getScoreboardManager()
                    .mainScoreboard
                    .getPlayerTeam(Bukkit.getOfflinePlayer(uuid))
                    ?.name // use null key for non-teamed player
            ) { potentialAreas.get() }

    override fun randomize() {
        teamAssigns = teamAssigns.mapValues { potentialAreas.get() }.toMutableMap()
    }
}
