package gg.uhc.migration.selection;

import com.google.common.collect.Maps;
import gg.uhc.migration.Area;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

public class TeamAreaSelection extends AreaSelection {

    protected final Map<String, Area> teamAssigns = Maps.newHashMap();

    @Override
    public Area getForUUID(UUID uuid) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(uuid));

        // use null key for non-teamed players
        String teamName;
        if (team == null) {
            teamName = null;
        } else {
            teamName = team.getName();
        }

        Area area = teamAssigns.get(teamName);

        if (area == null) {
            area = potentialAreas.random();
            teamAssigns.put(teamName, area);
        }

        return area;
    }

    @Override
    public void randomize() {
        for (String team : teamAssigns.keySet()) {
            teamAssigns.put(team, potentialAreas.random());
        }
    }
}
