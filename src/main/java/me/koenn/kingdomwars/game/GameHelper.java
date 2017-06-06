package me.koenn.kingdomwars.game;

import me.koenn.core.misc.Timer;
import me.koenn.fakeblockapi.FakeBlock;
import me.koenn.fakeblockapi.FakeBlockAPI;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.classes.Class;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public final class GameHelper implements Listener {

    public static void loadPlayers(Game game) {
        for (Player player : game.getPlayers()) {
            Team team = PlayerHelper.getTeam(player);
            Map map = game.getMap();
            Location spawn = getSpawn(map, team);
            Class cl = getClass(player, team, game);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SURVIVAL);
            player.setBedSpawnLocation(spawn, true);

            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false));

            //TODO: Auto-generate url and make the clickable text more clear.
            Messager.clickableMessage(player, References.MAP.replace("%map%", map.getName()), "Click to open the map page", "http://blockgaming.org/staff/forums/index.php?threads/map-specific-lore.13/");
            Messager.playerMessage(player, References.CLASS.replace("%class%", cl.getName()));

            PlayerHelper.giveKit(player, cl.getKits()[0]);
        }

        teleportPlayers(game);
    }

    public static void capture(ControlPoint point, Game game) {
        Team lost = point.owningTeam;
        Team won = point.owningTeam.getOpponent();

        Messager.teamTitle(References.CAPTURE_WIN_TITLE, References.CAPTURE_WIN_SUBTITLE, won, game);
        Messager.teamTitle(References.CAPTURE_LOSS_TITLE, References.CAPTURE_LOSS_SUBTITLE, lost, game);

        game.getMap().renderCapture(lost);

        for (ControlPoint controlPoint : game.getMap().getControlPoints()) {
            controlPoint.reset();
        }

        new Timer(20, KingdomWars.getInstance()).start(() -> teleportPlayers(game));
    }

    public static void teleportPlayers(Game game) {
        for (Player player : game.getPlayers()) {
            player.teleport(getSpawn(game.getMap(), PlayerHelper.getTeam(player)));
        }
    }

    public static Location getSpawn(Map map, Team team) {
        Location spawn = map.getSpawn(team);
        return spawn.getBlock() == null ? spawn : spawn.add(0.5, 1.0, 0.5);
    }

    public static Class getClass(Player player, Team team, Game game) {
        TeamInfo teamInfo = game.teams[team.getIndex()];
        return teamInfo.getClass(player);
    }

    @SuppressWarnings("deprecation")
    //TODO: Rewrite and move to Map class.
    public static void loadFakeBlocks(Game game) {
        Map map = game.getMap();
        Location spawn = map.getSpawn(Team.RED);
        List<Player> teamRed = game.getTeam(Team.RED);
        Player[] redTeam = new Player[teamRed.size()];
        for (int i = 0; i < redTeam.length; i++) {
            redTeam[i] = teamRed.get(i);
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(KingdomWars.getInstance(), () -> {
            Bukkit.getLogger().info("Starting packet send...");
            long time = System.currentTimeMillis();
            final Chunk[] chunks = spawn.getWorld().getLoadedChunks().clone();
            int packets = 0;
            for (Chunk chunk : chunks) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 200; y++) {
                        for (int z = 0; z < 16; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            Material type = block.getType();
                            if ((type.equals(Material.WOOL) || type.equals(Material.STAINED_CLAY) || type.equals(Material.STAINED_GLASS) || type.equals(Material.STAINED_GLASS_PANE)) && (block.getData() == 14 || block.getData() == 11)) {
                                byte data = block.getData();
                                FakeBlockAPI.fakeBlockRegistry.register(new FakeBlock(block.getLocation(), type, (short) (data == 14 ? 11 : 14), redTeam));
                                packets++;
                            }
                        }
                    }
                }
            }
            Bukkit.getLogger().info("Send out " + packets + " packets!");
            long taken = System.currentTimeMillis() - time;
            Bukkit.getLogger().info("Taken " + taken + "ms");
        }, 20);
    }

    //TODO: Doesn't appear to work properly.
    public static void resetFakeBlocks(Game game) {
        game.getPlayers().forEach(FakeBlockAPI::resetPlayer);
    }
}
