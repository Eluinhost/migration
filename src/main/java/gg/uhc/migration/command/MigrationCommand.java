package gg.uhc.migration.command;

import com.google.common.base.Optional;
import gg.uhc.migration.MigrationRunnable;
import gg.uhc.migration.MigrationRunnableFactory;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;


public class MigrationCommand implements CommandExecutor, Listener {

    public static final String PERMISSION = "uhc.migration";

    public static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to run this command";
    public static final String USAGE = "" + ChatColor.BOLD + ChatColor.DARK_GRAY + "Usage: /migration start OR /migration stop";
    public static final String ALREADY_RUNNING = "" + ChatColor.BOLD + ChatColor.RED + "A migration task is already running, cancel the existing one first with: /migration stop";
    public static final String NOT_RUNNING = "" + ChatColor.BOLD + ChatColor.RED + "There is no migration task already running, start one with: /migration start";
    public static final String STARTED = "" + ChatColor.BOLD + ChatColor.GREEN + "New migration task started";
    public static final String STOPPED = "" + ChatColor.BOLD + ChatColor.GREEN + "Stopped migration task";

    protected final MigrationRunnableFactory factory;
    protected final Plugin plugin;

    protected Optional<MigrationRunnable> current = Optional.absent();

    public MigrationCommand(Plugin plugin, MigrationRunnableFactory factory) {
        this.factory = factory;
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // permission check first
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        // invalid usage
        if (args.length == 0) {
            sender.sendMessage(USAGE);
            return true;
        }

        // start a timer if possible
        if (args[0].equalsIgnoreCase("start")) {
            if (current.isPresent()) {
                sender.sendMessage(ALREADY_RUNNING);
                return true;
            }

            MigrationRunnable runnable = factory.getNew();
            current = Optional.of(runnable);

            // once a second as required + register for events
            runnable.runTaskTimer(plugin, 0, 20);

            sender.sendMessage(STARTED);
            return true;
        }

        // stop a timer if we can
        if (args[0].equalsIgnoreCase("stop")) {
            if (!current.isPresent()) {
                sender.sendMessage(NOT_RUNNING);
                return true;
            }

            // cancel timer and set to no runnable
            current.get().cancel();
            current = Optional.absent();

            sender.sendMessage(STOPPED);
            return true;
        }

        sender.sendMessage(USAGE);
        return false;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        // attempt to apply missing damage on login if we need to
        // not sure if this is even required but why the hell not
        if (current.isPresent()) {
            current.get().attemptToApplyDamage(event.getPlayer());
        }
    }
}
