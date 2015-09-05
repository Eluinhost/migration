package gg.uhc.migration.configuration;

import com.google.common.collect.Lists;
import gg.uhc.migration.messages.MessageSender;
import gg.uhc.migration.messages.MultiMessageSender;
import gg.uhc.migration.messages.Senders;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;

public class MessageSenderReader extends ConfigurationReader<MessageSender> {
    @Override
    protected MessageSender read(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.isString(key)) throw up(WRONG_TYPE, key, "Message Sender Type", section.get(key));

        String all = section.getString(key);

        String[] types = all.split("\\+");

        List<MessageSender> senders = Lists.newArrayList();
        for(String type : types) {
            try {
                Senders sender = Senders.valueOf(type);

                senders.add(sender.sender);
            } catch (IllegalArgumentException e) {
                throw up(WRONG_TYPE, key, "Message Sender Type", type);
            }
        }

        return new MultiMessageSender(senders);
    }
}
