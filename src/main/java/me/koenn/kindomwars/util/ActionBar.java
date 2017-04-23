package me.koenn.kindomwars.util;

import me.koenn.kindomwars.KingdomWars;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBar {

    private String message;
    private int stay = 0;

    public ActionBar(String message) {
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public ActionBar setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public void send(Player p) {
        new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                if (time >= stay) {
                    cancel();
                    return;
                }
                IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
                PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte) 2);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(bar);
                time++;
            }
        }.runTaskTimer(KingdomWars.getInstance(), 0L, 20L);
    }
}
