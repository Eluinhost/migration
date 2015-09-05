package gg.uhc.migration.messages;

import gg.uhc.migration.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionbarMessageSender implements MessageSender {

    protected Constructor<?> chatComponentTextConstructor;
    protected Constructor<?> packetPlayerOutChatConstructor;
    protected Method getHandleMethod;
    protected Field playerConnectionField;
    protected Method sendPacketMethod;
    protected Class<?> packetClass;

    protected static final byte ACTION_BAR_TYPE = 2;

    protected boolean initialized = false;

    public ActionbarMessageSender() {
        try {
            // ChatComponentText constructor for new objects
            chatComponentTextConstructor = ReflectionUtils.getNMSClass("ChatComponentText").getConstructor(String.class);

            // PacketPlayOutChat constructor for new objects
            packetPlayerOutChatConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat").getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);

            // method CraftPlayer#getHandle() -> EntityPlayer
            getHandleMethod = ReflectionUtils.getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getHandle");

            // field EntityPlayer.playerConnection
            playerConnectionField = ReflectionUtils.getNMSClass("EntityPlayer").getField("playerConnection");

            // packet class
            packetClass = ReflectionUtils.getNMSClass("Packet");

            // method PlayerConnection#sendPacket(Packet)
            sendPacketMethod = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", packetClass);

            initialized = true;
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPlayerMessage(Player player, String message) {
        if (!initialized) {
            Bukkit.getConsoleSender().sendMessage("Migration: Attempted to send an actionbar when reflection has not been set up. Is the server running Spigot 1.8.8+?");
            return;
        }

        try {
            // make a new chat message
            Object chat = chatComponentTextConstructor.newInstance(message);

            // create a packet with the message in the action bar slot
            Object packet = packetPlayerOutChatConstructor.newInstance(chat, ACTION_BAR_TYPE);

            // grab the nms EntityPlayer
            Object entityPlayer = getHandleMethod.invoke(player);

            // grab their PlayerConnection
            Object playerConnection = playerConnectionField.get(entityPlayer);

            // send the packet
            sendPacketMethod.invoke(playerConnection, packet);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
