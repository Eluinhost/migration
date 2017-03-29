package gg.uhc.migration.configuration

import gg.uhc.migration.messages.MessageSender
import gg.uhc.migration.messages.MultiMessageSender
import gg.uhc.migration.messages.Senders
import org.bukkit.configuration.ConfigurationSection

fun ConfigurationSection.toMessageSender(key: String) : MessageSender {
    if (!this.isString(key))
        throw wrongType(key, "Message Sender Type", this.get(key))

    val types = this
        .getString(key)
        .split('+')
        .map(String::trim)
        .filterNot(String::isEmpty)
        .map(String::toUpperCase)
        .distinct()
        .map {
            try {
                Senders.valueOf(it)
            } catch (e: IllegalArgumentException) {
                throw wrongType(key, "Message Sender Type", it)
            }
        }
        .map { it.sender }

    return MultiMessageSender(types)
}
