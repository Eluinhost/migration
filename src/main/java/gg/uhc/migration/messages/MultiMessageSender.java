package gg.uhc.migration.messages;

import org.bukkit.entity.Player;

import java.util.List;

public class MultiMessageSender implements MessageSender {

    protected final List<MessageSender> wrapped;

    public MultiMessageSender(List<MessageSender> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void sendPlayerMessage(Player player, String message) {
        for (MessageSender sender : wrapped) {
            sender.sendPlayerMessage(player, message);
        }
    }
}
