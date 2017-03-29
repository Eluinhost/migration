package gg.uhc.migration.messages

import org.bukkit.entity.Player

class ChatMessageSender : MessageSender {
    override fun sendPlayerMessage(player: Player, message: String) = player.sendMessage(message)
}
