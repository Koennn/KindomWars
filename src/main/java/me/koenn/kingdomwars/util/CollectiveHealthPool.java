package me.koenn.kingdomwars.util;

import me.koenn.kingdomwars.KingdomWars;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CollectiveHealthPool implements Listener, Runnable {

    /**
     * Multiplier for all healing of the pool.
     */
    private static final double HEAL_MULTIPLIER = 0.8;

    /**
     * Multiplier for all damage to the pool.
     */
    private static final double DAMAGE_MULTIPLIER = 1.0;

    /**
     * All currently online players in the pool.
     */
    private final List<Player> players;

    /**
     * All player registered in to the pool.
     * Can include offline players.
     */
    private final List<UUID> registered;

    /**
     * Maximum health in the pool.
     */
    private final double maxHealth;

    /**
     * Id of the repeating run task.
     */
    private final int taskId;

    /**
     * Current health in the pool.
     */
    private double health;

    /**
     * Constructor to create a new CollectiveHealthPool.
     *
     * @param maxHealth maximum health in the pool
     */
    public CollectiveHealthPool(final double maxHealth, final Player[] players) {
        //Create and set properties.
        this.players = new ArrayList<>();
        this.registered = new ArrayList<>();
        this.maxHealth = maxHealth;

        //Add all players to the pool.
        Collections.addAll(this.players, players);

        //Register the players and set their max health.
        this.players.forEach(player -> {
            this.registered.add(player.getUniqueId());
            player.setMaxHealth(maxHealth);
        });

        //Register this to Bukkit as an event listener.
        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());

        //Start the repeating task for the run method.
        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, 2);
    }

    /**
     * Apply a certain amount of damage to the health pool.
     *
     * @param amount amount of damage to apply
     */
    private void damage(double amount, EntityDamageEvent cause) {
        //Set the pool's health, lower cap at 0.
        this.health = Math.max(this.health - amount, 0);

        //Check if the pool's health is below 0.5
        if (this.health < 0.5) {

            //Kill all players in the pool with the last damage cause.
            this.players.forEach(player -> {
                player.setLastDamageCause(cause);
                player.setHealth(0);
            });

            //Set the pool's health back to the pool's max health.
            this.health = this.maxHealth;
        }
    }

    /**
     * Heal the health pool for a certain amount.
     *
     * @param amount amount to heal for
     */
    private void heal(double amount) {
        //Set the pool's health with the upper cap at the pool's max health.
        this.health = Math.min(this.health + amount, this.maxHealth);
    }

    /**
     * Check if the health pool contains the player in the event.
     *
     * @param event PlayerEvent or EntityEvent
     * @return boolean containsPlayer
     */
    private boolean containsPlayer(Event event) {
        //Check if the event is a PlayerEvent or EntityEvent.
        if (event instanceof PlayerEvent) {

            //Check if the pool contains the player.
            return this.players.contains(((PlayerEvent) event).getPlayer());

        } else if (event instanceof EntityEvent) {

            //Check if the entity is a player and the pool contains the player.
            return ((EntityEvent) event).getEntity() instanceof Player && this.players.contains(((EntityEvent) event).getEntity());
        }

        //Otherwise, return false.
        return false;
    }

    /**
     * Check if a player is registered to this health pool.
     *
     * @param player Player to check
     * @return boolean isRegistered
     */
    private boolean isRegistered(Player player) {
        //Loop over all registered uuid's.
        for (UUID uuid : this.registered) {

            //Check if the uuid matches the player's uuid.
            if (uuid.equals(player.getUniqueId())) {
                return true;
            }
        }

        //If nothing matches, return false.
        return false;
    }

    /**
     * Disable and remove this CollectiveHealthPool.
     * Resets all players in the pool to the default max and full health.
     */
    public void disable() {
        //Cancel the repeating run task.
        Bukkit.getScheduler().cancelTask(this.taskId);

        //Unregister this listener from Bukkit.
        HandlerList.unregisterAll(this);

        //Reset the max health and health for the players in the pool.
        this.players.forEach(player -> {
            player.resetMaxHealth();
            player.setHealth(player.getMaxHealth());
        });

        //Clear all the player from the pool.
        this.players.clear();
        this.registered.clear();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        //Check if the health pool contains the player.
        if (this.containsPlayer(event)) {

            //Cancel the event.
            event.setCancelled(true);

            //Apply the damage to the health pool.
            this.damage(event.getFinalDamage() * DAMAGE_MULTIPLIER, event);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        //Check if the health pool contains the player.
        if (this.containsPlayer(event)) {

            //Cancel the event.
            event.setCancelled(true);

            //Apply the healing to the health pool.
            this.heal(event.getAmount() * HEAL_MULTIPLIER);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        //Check if the health pool contains the player.
        if (this.containsPlayer(event)) {

            //Remove the player from the health pool. (Player remains registered for if he rejoins)
            this.players.remove(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        //Check if the player is registered to the health pool.
        if (this.isRegistered(event.getPlayer())) {

            //Add the player back to the health pool.
            this.players.add(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        //Check if the plugin that is disabling is KingdomWars.
        if (event.getPlugin().equals(KingdomWars.getInstance())) {

            //Disable this health pool.
            this.disable();
        }
    }

    @Override
    public void run() {
        //Check if the pool's health is over 0.5
        if (this.health >= 0.5) {

            //Apply the pool's health to all players in the pool.
            this.players.forEach(player -> player.setHealth(this.health));
        }
    }
}
