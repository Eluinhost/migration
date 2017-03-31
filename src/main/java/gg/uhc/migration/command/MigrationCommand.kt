package gg.uhc.migration.command

import gg.uhc.migration.Area
import gg.uhc.migration.MigrationRunnable
import gg.uhc.migration.selection.AreaSelection
import gg.uhc.migration.selection.GlobalAreaSelection
import gg.uhc.migration.selection.IndividualAreaSelection
import gg.uhc.migration.selection.TeamAreaSelection
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin


class MigrationCommand(private val plugin: Plugin, private val factory: (AreaSelection) -> MigrationRunnable) : CommandExecutor, Listener {
    var current: MigrationRunnable? = null

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // permission check first
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(NO_PERMISSION)
            return true
        }

        // invalid usage
        if (args.isEmpty()) {
            sender.sendMessage(USAGE)
            return true
        }

        // start a timer if possible
        if (args[0].equals("start", ignoreCase = true)) {
            if (current != null) {
                sender.sendMessage(ALREADY_RUNNING)
                return true
            }

            if (args.size < 2) {
                sender.sendMessage(USAGE)
                return true
            }

            val selection = when (args[1].toLowerCase()) {
                "global" -> GlobalAreaSelection()
                "individual" -> IndividualAreaSelection()
                "team" -> TeamAreaSelection()
                else -> {
                    sender.sendMessage(USAGE)
                    return true
                }
            }

            val runnable = factory(selection)
            current = runnable

            // once a second as required + register for events
            runnable.runTaskTimer(plugin, 0, 20)

            sender.sendMessage(STARTED)
            return true
        }

        // stop a timer if we can
        if (args[0].equals("stop", ignoreCase = true)) {
            if (current == null) {
                sender.sendMessage(NOT_RUNNING)
                return true
            }

            // cancel timer and set to no runnable
            current?.cancel()
            current = null

            sender.sendMessage(STOPPED)
            return true
        }

        sender.sendMessage(USAGE)
        return false
    }

    @EventHandler
    fun on(event: PlayerJoinEvent) {
        // attempt to apply missing damage on login if we need to
        // not sure if this is even required but why the hell not
        current?.attemptToApplyDamage(event.player)
    }

    companion object {
        val PERMISSION = "uhc.migration"

        val NO_PERMISSION = ChatColor.RED.toString() + "You do not have permission to run this command"
        val USAGE = "" + ChatColor.BOLD + ChatColor.DARK_GRAY + "Usage: /migration start <global|individual|team> OR /migration stop"
        val ALREADY_RUNNING = "" + ChatColor.BOLD + ChatColor.RED + "A migration task is already running, cancel the existing one first with: /migration stop"
        val NOT_RUNNING = "" + ChatColor.BOLD + ChatColor.RED + "There is no migration task already running, start one with: /migration start"
        val STARTED = "" + ChatColor.BOLD + ChatColor.GREEN + "New migration task started"
        val STOPPED = "" + ChatColor.BOLD + ChatColor.GREEN + "Stopped migration task"
    }
}
