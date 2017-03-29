package gg.uhc.migration

import gg.uhc.migration.command.MigrationCommand
import gg.uhc.migration.configuration.*
import gg.uhc.migration.selection.AreaSelection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.plugin.java.JavaPlugin

class Entry : JavaPlugin() {
    override fun onEnable() {
        config.options().copyDefaults(true)
        saveConfig()

        try {
            val areas = config.toAreaList("areas")

            val damagePhaseSeconds = config.toInteger("seconds for damaging")
            val warningPhaseSeconds = config.toInteger("seconds for warning")

            val damageSender = config.toMessageSender("damage sender")
            val phaseStartSender = config.toMessageSender("phase change sender")
            val updatesSender = config.toMessageSender("timer update sender")

            val secondsPerHalfHeart = config.toInteger("seconds for half heart")
            val notificationTicks = config.toLongList("notification seconds")

            val messages = config.toCustomMessages("messages")

            val factory = { s: AreaSelection ->
                s.setPotentialAreas(areas)

                MigrationRunnable(
                    selection = s,
                    phaseStartSender = phaseStartSender,
                    updatesSender = updatesSender,
                    damageSender = damageSender,
                    warningPhaseSeconds = warningPhaseSeconds,
                    damagePhaseSeconds = damagePhaseSeconds,
                    secondsForHalfHeart = secondsPerHalfHeart,
                    notificationTicks = notificationTicks,
                    messages = messages
                )
            }

            getCommand("migration").executor = MigrationCommand(this, factory)
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
            isEnabled = false
        }
    }
}
