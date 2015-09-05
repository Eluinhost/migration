package gg.uhc.migration.messages;

import org.bukkit.entity.Player;

public class TitleMessageSender implements MessageSender {
    @Override
    public void sendPlayerMessage(Player player, String message) {
        //noinspection deprecation
        player.sendTitle(message, "");
    }
}
