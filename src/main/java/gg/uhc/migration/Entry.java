package gg.uhc.migration;

import gg.uhc.migration.command.MigrationCommand;
import gg.uhc.migration.configuration.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Entry extends JavaPlugin {

    @Override
    public void onEnable() {
        FileConfiguration configuration = getConfig();
        configuration.options().copyDefaults(true);
        saveConfig();

        AreaListReader areaListReader = new AreaListReader(new AreaReader(new RangeReader(new RangeParser())));
        PositiveIntegerReader integerReader = new PositiveIntegerReader(false);
        MessageSenderReader senderReader = new MessageSenderReader();
        LongListReader longListReader = new LongListReader();
        PlayerStringsReader stringsReader = new PlayerStringsReader();

        try {
            MigrationRunnableFactory factory = new MigrationRunnableFactory();

            // setup factory with config settings
            factory
                    // areas
                    .areas(areaListReader.readFromSection(configuration, "areas"))

                    // phase timers
                    .damagePhaseTime(integerReader.readFromSection(configuration, "seconds for damaging"))
                    .warningPhaseTime(integerReader.readFromSection(configuration, "seconds for warning"))

                    // senders
                    .damageSender(senderReader.readFromSection(configuration, "damage sender"))
                    .phaseStartSender(senderReader.readFromSection(configuration, "phase change sender"))
                    .updatesSender(senderReader.readFromSection(configuration, "timer update sender"))

                    // damage time
                    .secondsPerHalfHeart(integerReader.readFromSection(configuration, "seconds for half heart"))
                    .notificationTicks(longListReader.readFromSection(configuration, "notification seconds"))

                    // strings
                    .playerStrings(stringsReader.readFromSection(configuration, "messages"));

            getCommand("migration").setExecutor(new MigrationCommand(this, factory));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            setEnabled(false);
        }
    }
}
