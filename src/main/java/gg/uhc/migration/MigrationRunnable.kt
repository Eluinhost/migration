package gg.uhc.migration

import com.google.common.collect.Maps
import gg.uhc.migration.messages.MessageSender
import gg.uhc.migration.selection.AreaSelection
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class MigrationRunnable (
    // Holds the current selected areas we will keep checking
    private var selection: AreaSelection,
    // stores messages for particular actions
    private val phaseStartSender: MessageSender,
    private val updatesSender: MessageSender,
    private val damageSender: MessageSender,

    // how many seconds to count before switching to the next area
    private val warningPhaseSeconds: Int,
    private val damagePhaseSeconds: Int,

    // how many seconds for a half heart to be applied
    private val secondsForHalfHeart: Int,
    private val notificationTicks: List<Long>,
    private val messages: CustomMessages
) : BukkitRunnable() {
    private enum class Phase {
        WARNING,
        DAMAGING
    }

    // Stores how many seconds of damage the player has accumulated
    private val owedDamage: MutableMap<UUID, Int> = Maps.newHashMap<UUID, Int>()

    // current running phase
    private var currentPhase = Phase.WARNING

    // current timer for current phase
    private var countdown: Long = 0

    init {
        enterWarningPhase()
    }

    private fun enterWarningPhase() {
        currentPhase = Phase.WARNING
        countdown = warningPhaseSeconds.toLong()

        // randomize the area to warn about
        selection.randomize()

        // warn everyone with a title just the once
        Bukkit
            .getOnlinePlayers()
            .map { Pair(it, messages["GET TO AREA", selection.getForUUID(it.uniqueId).announce, countdown]) }
            .forEach { phaseStartSender.sendPlayerMessage(it.first, it.second) }
    }

    private fun enterDamagingPhase() {
        currentPhase = Phase.DAMAGING
        countdown = damagePhaseSeconds.toLong()

        // tell people with a title just the once
        val message = messages["DAMAGE PHASE START"]
        Bukkit
            .getOnlinePlayers()
            .forEach {
                phaseStartSender.sendPlayerMessage(it, message)
            }
    }

    override fun run() {
        countdown--

        // update the countdown if it ran out
        if (countdown == 0L) {
            when (currentPhase) {
                Phase.WARNING -> enterDamagingPhase()
                else -> enterWarningPhase()
            }

            // don't do anything else this second
            return
        }

        val announceUpdate = notificationTicks.contains(countdown)

        if (currentPhase == Phase.WARNING) {
            // update people with how long they have to get to the area
            if (announceUpdate) {
                Bukkit
                    .getOnlinePlayers()
                    .map { Pair(it, messages["GET TO AREA", selection.getForUUID(it.uniqueId), countdown])}
                    .forEach { updatesSender.sendPlayerMessage(it.first, it.second) }
            }

            return
        }

        // Phase.DAMAGING

        Bukkit
            .getOnlinePlayers()
            .map { Pair(it, selection.getForUUID(it.uniqueId)) }
            .forEach { (p, area) ->
                val location = p.location

                val inside = area.areCoordinatesInside(location.x, location.z)

                // if they are not inside the current selection add a point of damage to them
                if (!inside) {

                    // add one onto the owed damage and pay any health needed
                    when (p.gameMode) {
                        GameMode.SURVIVAL, GameMode.ADVENTURE -> {
                            val damage = owedDamage.getOrDefault(p.uniqueId, 0) + 1
                            owedDamage.put(p.uniqueId, damage)
                            attemptToApplyDamage(p)
                        }
                        else -> {}
                    }
                }

                if (announceUpdate) {
                    updatesSender.sendPlayerMessage(p, messages["DAMAGE TICK", area.announce, countdown])
                }
            }
    }

    /**
     * Checks accumulated damage for the player and applies it as required

     * @param player the player to check for
     */
    fun attemptToApplyDamage(player: Player) {
        var points: Int = owedDamage[player.uniqueId] ?: return

        val times = points / secondsForHalfHeart
        points %= secondsForHalfHeart

        owedDamage.put(player.uniqueId, points)

        // if they owe health
        if (times > 0) {
            player.health = Math.max(0.0, player.health - times)
            damageSender.sendPlayerMessage(player, messages["DAMAGE NOTIFICATION", times])
        }
    }
}
