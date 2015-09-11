package gg.uhc.migration;

import com.google.common.collect.Maps;
import gg.uhc.migration.messages.MessageSender;
import gg.uhc.migration.selection.AreaSelection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MigrationRunnable extends BukkitRunnable {

    enum Phase {
        WARNING,
        DAMAGING
    }

    // weighted collection of all areas we can pull from
//    protected final RandomCollection<Area> potentialAreas;

    // how many seconds to count before switching to the next area
    protected final int warningPhaseSeconds;
    protected final int damagePhaseSeconds;

    // how many seconds for a half heart to be applied
    protected final int secondsForHalfHeart;

    // stores messages for particular actions
    protected final MessageSender phaseStartSender;
    protected final MessageSender updatesSender;
    protected final MessageSender damageSender;

    // Stores how many seconds of damage the player has accumulated
    protected final Map<UUID, PlayerDamagePoints> owedDamage = Maps.newHashMap();

    // Holds the current selected areas we will keep checking
    protected AreaSelection selection;

    // current running phase
    protected Phase currentPhase = Phase.WARNING;

    // current timer for current phase
    protected long countdown;

    protected final List<Long> notificationTicks;
    protected final PlayerStrings _;

    protected MigrationRunnable(
            AreaSelection selection,
            MessageSender phaseStartSender, MessageSender updatesSender, MessageSender damageSender,
            int warningPhaseSeconds, int damagePhaseSeconds,
            int secondsForHalfHeart,
            List<Long> notificationTicks,
            PlayerStrings _)
    {
        this.selection = selection;
        this.phaseStartSender = phaseStartSender;
        this.updatesSender = updatesSender;
        this.damageSender = damageSender;
        this.warningPhaseSeconds = warningPhaseSeconds;
        this.damagePhaseSeconds = damagePhaseSeconds;
        this.secondsForHalfHeart = secondsForHalfHeart;
        this.notificationTicks = notificationTicks;
        this._ = _;

        enterWarningPhase();
    }

    protected void enterWarningPhase() {
        currentPhase = Phase.WARNING;
        countdown = warningPhaseSeconds;

        // randomize the area to warn about
        selection.randomize();

        // warn everyone with a title just the once
        for (Player player : Bukkit.getOnlinePlayers()) {
            Area area = selection.getForUUID(player.getUniqueId());
            String message = _.format("GET TO AREA", area.getAnnounce(), countdown);

            phaseStartSender.sendPlayerMessage(player, message);
        }
    }

    protected void enterDamagingPhase() {
        currentPhase = Phase.DAMAGING;
        countdown = damagePhaseSeconds;

        // tell people with a title just the once
        String message = _.format("DAMAGE PHASE START");
        for (Player player : Bukkit.getOnlinePlayers()) {
            phaseStartSender.sendPlayerMessage(player, message);
        }
    }

    @Override
    public void run() {
        countdown--;

        // update the countdown if it ran out
        if (countdown == 0) {
            if (currentPhase == Phase.WARNING) {
                enterDamagingPhase();
            } else {
                enterWarningPhase();
            }

            // don't do anything else this second
            return;
        }

        boolean announceUpdate = notificationTicks.contains(countdown);

        if (currentPhase == Phase.WARNING) {
            // update people with how long they have to get to the area
            if (announceUpdate) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Area area = selection.getForUUID(player.getUniqueId());
                    String message = _.format("GET TO AREA", area.getAnnounce(), countdown);

                    updatesSender.sendPlayerMessage(player, message);
                }
            }

            return;
        }

        // Phase.DAMAGING

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            Area area = selection.getForUUID(player.getUniqueId());

            boolean inside = area.inside(location.getX(), location.getZ());

            // if they are not inside the current selection add a point of damage to them
            if (!inside) {
                PlayerDamagePoints damage = owedDamage.get(player.getUniqueId());

                // set if it doesn't exist
                if (damage == null) {
                    damage = new PlayerDamagePoints();
                    owedDamage.put(player.getUniqueId(), damage);
                }

                // add one onto the owed damage and pay any health needed
                GameMode mode = player.getGameMode();
                if (mode == GameMode.SURVIVAL || mode == GameMode.ADVENTURE) {
                    damage.increment();
                    attemptToApplyDamage(player);
                }
            }

            if (announceUpdate) {
                String message = _.format("DAMAGE TICK", area.getAnnounce(), countdown);
                updatesSender.sendPlayerMessage(player, message);
            }
        }
    }

    /**
     * Checks accumulated damage for the player and applies it as required
     *
     * @param player the player to check for
     */
    public void attemptToApplyDamage(Player player) {
        PlayerDamagePoints points = owedDamage.get(player.getUniqueId());

        // player hasn't taken any points yet
        if (points == null) return;

        int times = 0;
        while (points.get() >= secondsForHalfHeart) {
            times++;
            // remove the points and increase the amount of times we need to damage
            points.remove(secondsForHalfHeart);
        }

        // if they owe health
        if (times > 0) {
            player.setHealth(Math.max(0, player.getHealth() - (times)));
            damageSender.sendPlayerMessage(player, _.format("DAMAGE NOTIFICATION", times));
        }
    }
}
