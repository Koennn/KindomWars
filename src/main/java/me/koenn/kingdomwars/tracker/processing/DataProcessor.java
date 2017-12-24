package me.koenn.kingdomwars.tracker.processing;

import me.koenn.core.data.JSONManager;
import me.koenn.core.registry.Registry;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.events.*;
import me.koenn.kingdomwars.game.map.Map;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.json.simple.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class DataProcessor implements Listener {

    private static final SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static DataProcessor INSTANCE;

    private final Registry<MapData> mapData = new Registry<>(MapData::getName);
    private final Registry<PlayerData> playerData = new Registry<>(PlayerData::getName);

    public DataProcessor() {
        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());

        Map.maps.getRegisteredObjects().forEach(map -> this.mapData.register(new MapData(map)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(GameStartEvent event) {
        event.getGame().getPlayers().stream().filter(player -> this.playerData.get(player.getUniqueId().toString()) == null).forEach(player -> this.playerData.register(new PlayerData(player.getUniqueId())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePointDefended(GamePointDefendedEvent event) {
        MapData map = this.mapData.get(event.getGame().getMap().getName());
        map.totalDefenses[event.getTeam().getIndex()]++;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePointCap(GamePointCapEvent event) {
        MapData map = this.mapData.get(event.getGame().getMap().getName());
        map.totalCaptures[event.getCaptured().getIndex()]++;

        List<Player>[] onPoint = event.getOnPoint();
        for (Team team : Team.values()) {
            onPoint[team.getIndex()].forEach(player -> this.playerData.get(player.getUniqueId().toString()).totalCaptures++);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameKill(GameKillEvent event) {
        MapData map = this.mapData.get(event.getGame().getMap().getName());
        map.totalKills[PlayerHelper.getTeam(event.getKiller()).getIndex()]++;

        PlayerData killer = this.playerData.get(event.getKiller().getUniqueId().toString());
        killer.totalKills++;

        PlayerData killed = this.playerData.get(event.getKilled().getUniqueId().toString());
        killed.totalDeaths++;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameFinish(GameFinishEvent event) {
        MapData data = this.mapData.get(event.getGame().getMap().getName());
        data.totalMatches++;

        for (Team team : Team.values()) {
            event.getGame().getTeam(team).forEach(player -> {
                if (team.equals(event.getWinner())) {
                    this.playerData.get(player.getUniqueId().toString()).totalWins++;
                } else {
                    this.playerData.get(player.getUniqueId().toString()).totalLosses++;
                }
            });
        }


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        if (!event.getPlugin().equals(KingdomWars.getInstance())) {
            return;
        }

        JSONManager dataFile = new JSONManager(KingdomWars.getInstance(), String.format("%s_", timestamp.format(new Date())));

        JSONArray maps = new JSONArray();
        JSONArray players = new JSONArray();
        this.mapData.getRegisteredObjects().forEach(mapData -> maps.add(mapData.toJSON()));
        this.playerData.getRegisteredObjects().forEach(playerData -> players.add(playerData.toJSON()));

        dataFile.setInBody("maps", maps);
        dataFile.setInBody("players", players);

        System.out.println(dataFile.getBody().toJSONString());
    }
}
