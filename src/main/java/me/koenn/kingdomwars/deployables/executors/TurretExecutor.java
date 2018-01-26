package me.koenn.kingdomwars.deployables.executors;

import me.koenn.kingdomwars.deployables.Deployable;
import me.koenn.kingdomwars.deployables.DeployableExecutor;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class TurretExecutor implements DeployableExecutor {

    private Game game;
    private Team team;
    private Player owner;
    private Player target;
    private int cooldown;
    private Deployable deployable;

    private static boolean isInRange(Player player, Deployable deployable) {
        return player.getGameMode().equals(GameMode.SURVIVAL) && player.getLocation().distance(deployable.getLocation()) < 10;
    }

    @Override
    public void init(Player owner, Deployable deployable) {
        this.game = PlayerHelper.getGame(owner);
        this.team = PlayerHelper.getTeam(owner);
        this.owner = owner;
        this.deployable = deployable;
    }

    @Override
    public void update() {
        if (this.game == null || this.team == null) {
            return;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }

        if (this.target == null || !isInRange(target, deployable)) {
            this.target = null;
            for (UUID player : this.game.getPlayers()) {
                if (PlayerHelper.getTeam(player) == this.team) {
                    continue;
                }
                if (isInRange(Bukkit.getPlayer(player), this.deployable)) {
                    this.target = Bukkit.getPlayer(player);
                }
            }
        }

        if (this.target != null) {
            Location loc2 = this.deployable.getLocation().clone().add(0.5, 2.5, 0.5);
            Location loc1 = this.target.getLocation().clone().add(0.0, 1.0 + (this.target.getLocation().distance(deployable.getLocation()) / 8), 0.0);
            Vector direction = new Vector(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ());
            Arrow arrow = loc2.getWorld().spawnArrow(loc2, direction, 1.5F, 0);
            arrow.setCustomName(this.owner.getName() + "'s Turret");
            arrow.setCustomNameVisible(false);
            this.cooldown = 12;
        }
    }
}
