package me.koenn.kingdomwars.deployables;

import org.bukkit.entity.Player;

public interface DeployableExecutor {

    void init(Player owner, Deployable deployable);

    void update();
}
