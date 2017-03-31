package gg.uhc.migration.messages

import org.bukkit.entity.Player

class TitleMessageSender : MessageSender {
    override fun sendPlayerMessage(player: Player, message: String) = player.sendTitle(message, "")
}
