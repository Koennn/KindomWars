package me.koenn.kingdomwars.game;

import me.koenn.core.data.JSONManager;
import me.koenn.core.misc.ColorHelper;
import me.koenn.core.misc.FancyString;
import me.koenn.core.misc.LocationHelper;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.discord.DiscordBot;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.util.Messager;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.References;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, April 2017
 */
public class GameCreator implements Runnable {

    public static final GameCreator instance = new GameCreator();

    private final HashMap<Sign, Game> games = new HashMap<>();

    private GameCreator() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomWars.getInstance(), this, 0, 10);
    }

    @Override
    public void run() {
        this.games.keySet().forEach(sign -> {
            Game game = this.games.get(sign);
            int maxplayers = Math.toIntExact((long) game.getMap().getProperty("maxplayers"));
            sign.setLine(3, ColorHelper.readColor(References.SIGN_3
                    .replace("%color%", (game.isFull() || game.getCurrentPhase().equals(GamePhase.STARTED) ? ChatColor.RED : ChatColor.GREEN).toString())
                    .replace("%pcount%", String.valueOf(game.getPlayers().size()))
                    .replace("%maxp%", String.valueOf(maxplayers))
            ));
            if (game.getPlayers().size() + 1 == maxplayers) {
                sign.setLine(2, ColorHelper.readColor(References.LAST_SPOT));
            } else if (game.getPlayers().size() == maxplayers) {
                sign.setLine(2, ColorHelper.readColor(References.FULL));
            } else if (!sign.getLine(2).equals("")) {
                sign.setLine(2, "");
            }
            if (game.getCurrentPhase().equals(GamePhase.STARTING) || game.getCurrentPhase().equals(GamePhase.STARTED)) {
                sign.setLine(2, ColorHelper.readColor(References.SPECTATE));
            }
            sign.update();
        });
    }

    public void registerSign(Sign sign, String mapname) {
        Map map = Map.getMap(mapname);
        if (map == null) {
            return;
        }

        int maxplayers = Math.toIntExact((long) map.getProperty("maxplayers"));

        sign.setLine(0, ColorHelper.readColor(References.SIGN_0));
        sign.setLine(1, ColorHelper.readColor(References.SIGN_1.replace("%mapname%", new FancyString(mapname).toString())));
        sign.setLine(2, ColorHelper.readColor(References.SIGN_2));
        sign.setLine(3, ColorHelper.readColor(References.SIGN_3
                .replace("%color%", ChatColor.GREEN.toString())
                .replace("%pcount%", String.valueOf(0))
                .replace("%maxp%", String.valueOf(maxplayers))
        ));
        sign.update();
        sign.getBlock().getState().update();

        Game game = new Game(map);
        this.games.put(sign, game);
        this.saveSigns();
    }

    public boolean signClick(Sign sign, Player player) {
        Game game = this.games.get(sign);
        System.out.println(game);
        if (game.getCurrentPhase().equals(GamePhase.STARTED) || game.getCurrentPhase().equals(GamePhase.STARTING)) {
            player.teleport(game.getMap().getSpawn(Team.RED));
            Bukkit.getScheduler().scheduleSyncDelayedTask(KingdomWars.getInstance(), () -> player.setGameMode(GameMode.SPECTATOR), 20);
            game.getPlayers().add(player.getUniqueId());
            return false;
        }

        if (game == null || game.isFull() || PlayerHelper.isInGame(player.getUniqueId())) {
            return false;
        }

        game.addPlayer(player.getUniqueId());

        DiscordBot.attemptMovePlayer(player, game.getLobby().getLobbyId());

        Messager.playerMessage(player, References.JOIN_MESSAGE);

        if (game.isFull()) {
            game.load();
        }
        return true;
    }

    public void saveSigns() {
        File file = new File("signs.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONManager signFile = new JSONManager(KingdomWars.getInstance(), file.getName());
        JSONArray signs = new JSONArray();

        this.games.keySet().forEach(sign -> {
            String map = this.games.get(sign).getMap().getName();
            JSONObject signObject = new JSONObject();
            signObject.put("location", LocationHelper.getString(sign.getLocation()));
            signObject.put("map", map);
            signs.add(signObject);
        });

        signFile.setInBody("signs", signs);
    }

    public void loadSigns() {
        File file = new File("signs.json");
        if (!file.exists()) {
            return;
        }
        JSONManager signFile = new JSONManager(KingdomWars.getInstance(), file.getName());
        JSONArray signs = (JSONArray) signFile.getFromBody("signs");
        if (signs == null) {
            return;
        }

        signs.forEach(sign -> {
            try {
                JSONObject signObject = (JSONObject) sign;
                String map = (String) signObject.get("map");
                Location location = LocationHelper.fromString((String) signObject.get("location"));
                Sign signBlock = (Sign) location.getWorld().getBlockAt(location).getState();
                Game game = new Game(Map.getMap(map));
                this.games.put(signBlock, game);
            } catch (Exception ex) {
                KingdomWars.getInstance().getLogger().severe(
                        String.format("Sign %s doesn't exist in the world anymore!", ((JSONObject) sign).toJSONString())
                );
                ex.printStackTrace();
            }
        });
    }
}
