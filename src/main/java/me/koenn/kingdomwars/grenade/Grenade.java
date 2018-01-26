package me.koenn.kingdomwars.grenade;

import me.koenn.core.cgive.CItem;
import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.ItemHelper;
import me.koenn.core.misc.LoreHelper;
import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.util.HashMap;
import java.util.UUID;

/**
 * Object representing a specific stack of grenades.
 */
public abstract class Grenade implements Listener, CItem {

    /**
     * Map of the currently active (thrown) grenade executor objects.
     */
    private final HashMap<UUID, GrenadeExecutor> executors = new HashMap<>();

    /**
     * Map of the currently active (thrown) grenade 'reload' tasks.
     */
    private final HashMap<UUID, Integer> tasks = new HashMap<>();

    /**
     * Name, representing the type of grenade.
     */
    private final String name;

    /**
     * UUID of this specific grenade stack.
     */
    private final UUID uuid;

    /**
     * Create a new Grenade stack object.
     *
     * @param name Name, representing the type of grenade
     */
    protected Grenade(String name) {
        this.name = name;
        this.uuid = UUID.randomUUID();

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
    }

    /**
     * Remove this grenade stack, disables all active (thrown) and inactive grenades.
     */
    public void remove() {
        HandlerList.unregisterAll(this);
        this.tasks.values().forEach(task -> Bukkit.getScheduler().cancelTask(task));
    }

    /**
     * Create a new GrenadeExecutor for grenade functionality.
     *
     * @return GrenadeExecutor object
     */
    protected abstract GrenadeExecutor createExecutor();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        //Check if the Projectile is a snowball and is thrown by a player.
        if (!event.getEntity().getType().equals(EntityType.SNOWBALL) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        //Create variables.
        Player player = (Player) event.getEntity().getShooter();
        Projectile grenade = event.getEntity();
        ItemStack item = player.getInventory().getItemInMainHand();

        //Check if the thrown grenade matches this stack.
        if (!item.isSimilar(this.getItem())) {
            return;
        }

        //Give the Projectile our grenade metadata.
        grenade.setMetadata("grenade", new GrenadeMeta(this.name));

        //Create a new GrenadeExecutor for this grenade.
        GrenadeExecutor executor = this.createExecutor();

        //Call the onThrow method of the executor to let it handle functionality.
        executor.onThrow(player, grenade);

        //Save the executor in the Map.
        this.executors.put(grenade.getUniqueId(), executor);

        //Create a task that gives the grenade back to the player after 800 ticks.
        this.giveGrenade(grenade, player.getUniqueId());
    }

    private void giveGrenade(Projectile grenade, UUID uuid) {
        this.tasks.put(
                grenade.getUniqueId(),
                Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> {
                    this.tasks.remove(grenade.getUniqueId());
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        this.giveGrenade(grenade, uuid);
                        return;
                    }
                    player.getInventory().addItem(this.getItem());
                }, 800)
        );
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        //Check if the Projectile is a snowball.
        if (!event.getEntity().getType().equals(EntityType.SNOWBALL)) {
            return;
        }

        //Check if the Projectile has our grenade metadata.
        if (event.getEntity().getMetadata("grenade").isEmpty()) {
            return;
        }

        //Read out the GrenadeMeta object.
        GrenadeMeta meta = (GrenadeMeta) event.getEntity().getMetadata("grenade").get(0);

        //Check if the grenade name (type) matches this stack.
        if (!meta.value().equals(this.name)) {
            return;
        }

        //Store the Projectile to a variable.
        Projectile grenade = event.getEntity();

        //Attempt to find a GrenadeExecutor for this grenade.
        GrenadeExecutor executor = this.executors.get(grenade.getUniqueId());

        //Check if we successfully found one.
        if (executor == null) {
            return;
        }

        //Call the executors onImpact method to let it handle functionality.
        executor.onImpact(grenade.getLocation());
    }

    @Override
    public ItemStack getItem() {
        return ItemHelper.makeItemStack(
                Material.SNOW_BALL, 1, (short) 0,
                ChatColor.RESET + new FancyString(this.name).toString(),
                LoreHelper.makeLore(ChatColor.GRAY + "" + ChatColor.ITALIC + this.uuid.toString())
        );
    }

    @Override
    public String getName() {
        return this.name;
    }
}
