package gg.uhc.migration.messages;

import org.bukkit.entity.Player;

public class ChatMessageSender implements MessageSender {
    @Override
    public void sendPlayerMessage(Player player, String message) {
        player.sendMessage(message);
    }
}
