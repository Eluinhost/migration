package gg.uhc.migration.messages

import org.bukkit.entity.Player

class MultiMessageSender(private val wrapped: List<MessageSender>) : MessageSender {
    override fun sendPlayerMessage(player: Player, message: String) = wrapped.forEach { it.sendPlayerMessage(player, message) }
}
