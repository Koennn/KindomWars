package me.koenn.kingdomwars.grenade;

import me.koenn.core.cgive.CItem;
import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.ItemHelper;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Grenade implements Listener, CItem, Runnable {

    private final String name;
    private final int taskId;

    public Grenade(String name) {
        this.name = name;

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, 5);
    }

    public void remove() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTask(this.taskId);
    }

    protected abstract void onThrow(Player thrower, Projectile grenade);

    protected abstract void onImpact(Location impact);

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!event.getEntity().getType().equals(EntityType.SNOWBALL) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        Projectile grenade = event.getEntity();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!item.isSimilar(this.getItem())) {
            return;
        }

        grenade.setMetadata("grenade", new GrenadeMeta(this.name));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!event.getEntity().getType().equals(EntityType.SNOWBALL)) {
            return;
        }

        if (event.getEntity().getMetadata("grenade").isEmpty()) {
            return;
        }

        GrenadeMeta meta = (GrenadeMeta) event.getEntity().getMetadata("grenade").get(0);
        if (!meta.value().equals(this.name)) {
            return;
        }

        this.onImpact(event.getEntity().getLocation());
    }

    @Override
    public ItemStack getItem() {
        return ItemHelper.makeItemStack(
                Material.SNOW_BALL, 1, (short) 0,
                ChatColor.RESET + new FancyString(this.name).toString(),
                null
        );
    }

    @Override
    public String getName() {
        return this.name;
    }
}
