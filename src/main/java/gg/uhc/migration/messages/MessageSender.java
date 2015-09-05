package gg.uhc.migration.messages;

import org.bukkit.entity.Player;

public interface MessageSender {
    void sendPlayerMessage(Player player, String message);
}
