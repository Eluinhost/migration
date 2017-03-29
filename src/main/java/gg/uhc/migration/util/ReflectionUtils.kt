package gg.uhc.migration.util

import org.bukkit.Bukkit

val nmsPacketVersion = lazy {
    val name = Bukkit.getServer().javaClass.`package`.name
    name.substring(name.lastIndexOf('.') + 1)
}

@Throws(ClassNotFoundException::class)
fun getNMSClass(subPackage: String): Class<*> = Class.forName("net.minecraft.server.$nmsPacketVersion.$subPackage")

@Throws(ClassNotFoundException::class)
fun getCraftBukkitClass(subPackage: String): Class<*> = Class.forName("org.bukkit.craftbukkit.$nmsPacketVersion.$subPackage")