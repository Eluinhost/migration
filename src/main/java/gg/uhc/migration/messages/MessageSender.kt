package gg.uhc.migration.messages

import org.bukkit.entity.Player

interface MessageSender {
    fun sendPlayerMessage(player: Player, message: String)
}
