package gg.uhc.migration.messages

import gg.uhc.migration.util.getCraftBukkitClass
import gg.uhc.migration.util.getNMSClass
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ActionbarMessageSender private constructor() : MessageSender {
    companion object {
        private val ACTION_BAR_TYPE: Byte = 2

        object ErroredActionbarMessageSender : MessageSender {
            override fun sendPlayerMessage(player: Player, message: String) =
                Bukkit.getConsoleSender().sendMessage("Migration: Attempted to send an actionbar when reflection has not been set up. Is the server running Spigot 1.8.8+?")
        }

        fun createInstance() = try {
            ActionbarMessageSender()
        } catch (e: Throwable) {
            ErroredActionbarMessageSender
        }
    }

    // ChatComponentText constructor for new objects
    private val chatComponentTextConstructor = getNMSClass("ChatComponentText").getConstructor(String::class.java)

    // PacketPlayOutChat constructor for new objects
    private val packetPlayerOutChatConstructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), Byte::class.javaPrimitiveType)

    // method CraftPlayer#getHandle() -> EntityPlayer
    private val getHandleMethod = getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle")

    // field EntityPlayer.playerConnection
    private val playerConnectionField = getNMSClass("EntityPlayer").getField("playerConnection")

    // method PlayerConnection#sendPacket(Packet)
    private val sendPacketMethod = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"))

    override fun sendPlayerMessage(player: Player, message: String) {
        try {
            // make a new chat message
            val chat = chatComponentTextConstructor.newInstance(message)

            // create a packet with the message in the action bar slot
            val packet = packetPlayerOutChatConstructor.newInstance(chat, ACTION_BAR_TYPE)

            // grab the nms EntityPlayer
            val entityPlayer = getHandleMethod.invoke(player)

            // grab their PlayerConnection
            val playerConnection = playerConnectionField.get(entityPlayer)

            // send the packet
            sendPacketMethod.invoke(playerConnection, packet)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
