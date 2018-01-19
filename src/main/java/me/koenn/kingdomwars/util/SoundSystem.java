package me.koenn.kingdomwars.util;

import me.koenn.kingdomwars.game.Game;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Helper class for playing sounds.
 */
public final class SoundSystem {

    /**
     * Plays a sound to all players in a game.
     *
     * @param game   Game to play in
     * @param sound  Sound to play
     * @param volume Volume to play at
     * @param pitch  Pitch to play at
     */
    public static void gameSound(Game game, Sound sound, float volume, float pitch) {
        game.getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, volume, pitch));
    }

    /**
     * Player a sound to a specific player.
     *
     * @param player Player to play to
     * @param sound  Sound to play
     * @param volume Volume to play at
     * @param pitch  Pitch to play at
     */
    public static void playerSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    /**
     * Play a sound at a specific location.
     *
     * @param location Location to play at
     * @param sound    Sound to play
     * @param volume   Volume to play at
     * @param pitch    Pitch to play at
     */
    public static void locationSound(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
}
