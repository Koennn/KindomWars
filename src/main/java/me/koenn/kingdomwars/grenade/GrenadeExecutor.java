package me.koenn.kingdomwars.grenade;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public interface GrenadeExecutor {

    void onThrow(Player thrower, Projectile grenade);

    void onImpact(Location impact);
}
