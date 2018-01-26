package me.koenn.kingdomwars.game.map;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.util.SoundSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class JumpPad implements Listener, Runnable {

    private static final HashMap<UUID, Integer> JUMPED = new HashMap<>();

    private final Location location;
    private int cooldown = 0;

    public JumpPad(Location location) {
        this.location = location.getBlock().getLocation();
        location.getBlock().setType(Material.WOOL);
        location.getBlock().setData((byte) 4);

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, 10);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getFrom().clone().add(0, -1, 0).getBlock().getLocation().equals(this.location) || this.cooldown > 0) {
            return;
        }

        double from = event.getFrom().getY() + 0.2;
        double to = event.getTo().getY();
        if (to > from) {
            event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 1.6, 0)));
            this.cooldown = 4;
            SoundSystem.locationSound(event.getFrom(), Sound.ENTITY_SLIME_JUMP, 2.0F, 1.2F);
            JUMPED.put(event.getPlayer().getUniqueId(), 80);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if (JUMPED.containsKey(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                SoundSystem.locationSound(event.getEntity().getLocation(), Sound.ENTITY_SLIME_JUMP, 2.0F, 1.2F);
                JUMPED.remove(event.getEntity().getUniqueId());
            }
        }
    }

    @Override
    public void run() {
        if (this.cooldown > 0) {
            this.cooldown--;
        }

        JUMPED.keySet().forEach(player -> JUMPED.put(player, JUMPED.get(player) - 5));

        List<UUID> remove = new ArrayList<>();

        JUMPED.keySet().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnGround()) {
                remove.add(uuid);
            }
        });
        remove.addAll(JUMPED.keySet().stream()
                .filter(player -> JUMPED.get(player) <= 0)
                .collect(Collectors.toList()));

        remove.forEach(JUMPED::remove);
    }
}
