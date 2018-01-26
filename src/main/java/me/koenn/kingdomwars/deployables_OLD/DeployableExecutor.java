package me.koenn.kingdomwars.deployables_OLD;

import me.koenn.kingdomwars.util.Team;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public interface DeployableExecutor {

    boolean construct(Player player);

    void constructComplete();

    void tick();

    void damage(int amount, Player damager);

    void destroy();

    Team getTeam();

    UUID getOwner();
}
