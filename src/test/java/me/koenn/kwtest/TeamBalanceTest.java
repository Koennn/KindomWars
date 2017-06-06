package me.koenn.kwtest;

import me.koenn.core.data.JSONManager;
import me.koenn.kingdomwars.game.TeamBalancer;
import me.koenn.kingdomwars.game.TeamInfo;
import me.koenn.kingdomwars.game.classes.ClassLoader;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.entity.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public class TeamBalanceTest {

    private final List<Player> players = new ArrayList<>();

    public TeamBalanceTest() {
        JSONManager.setVerbose(false);
        ClassLoader.setTest(true);
        ClassLoader.load();

        for (int i = 0; i < 6; i++) {
            this.players.add(new TestPlayer(ClassLoader.getRandomClass().getName(), ClassLoader.getRandomClass().getName()));
        }
    }

    @Test
    public void testTeamBalance() throws Exception {
        TeamBalancer balancer = new TeamBalancer(this.players, Team.RED);
        balancer.balance();
        TeamInfo info = balancer.getTeamInfo();
        assertNotNull(info);

        for (Player player : info.getPlayers()) {
            System.out.println("Player " + player.getName() + " preferred " + PlayerHelper.getMostPreferredClass(player).getName() + " and got " + info.getClass(player).getName());
        }

        assertTrue("Need more attackers", balancer.getClassSize(ClassLoader.getClass("Attacker")) >= 1);
        assertTrue("Need more defenders", balancer.getClassSize(ClassLoader.getClass("Defender")) >= 1);
        assertTrue("Need more builders", balancer.getClassSize(ClassLoader.getClass("Builder")) >= 1);
        assertTrue("Need more skirmishers", balancer.getClassSize(ClassLoader.getClass("Skirmisher")) >= 1);
    }
}
