package me.koenn.kingdomwars.tracker;

import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.game.Game;
import me.koenn.kingdomwars.game.events.GameFinishEvent;
import me.koenn.kingdomwars.game.events.GameKillEvent;
import me.koenn.kingdomwars.game.events.GamePointCapEvent;
import me.koenn.kingdomwars.game.events.GameStartEvent;
import me.koenn.kingdomwars.game.map.ControlPoint;
import me.koenn.kingdomwars.tracker.processing.SheetsAPI;
import me.koenn.kingdomwars.util.PlayerHelper;
import me.koenn.kingdomwars.util.Team;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameTracker implements Listener {

    private static final SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private final Game game;
    private final HashMap<String, List<Integer>> kills = new HashMap<>();
    private BufferedWriter writer;

    public GameTracker(Game game) {
        this.game = game;
    }

    public void enable() {
        File file = new File(KingdomWars.getInstance().getDataFolder(), timestamp.format(new Date()) + "-" + String.valueOf(game.hashCode()) + ".log");
        try {
            file.createNewFile();
            this.writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
            this.disable();
        }

        Bukkit.getPluginManager().registerEvents(this, KingdomWars.getInstance());
    }

    public void disable() {
        HandlerList.unregisterAll(this);

        if (this.writer != null) {
            try {
                this.writer.flush();
                this.writer.close();
                this.writer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameStart(GameStartEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        this.game.getPlayers().forEach(player -> {
            String character = PlayerHelper.getSelectedCharacter(player).getName();
            if (!this.kills.containsKey(character)) {
                this.kills.put(character, Arrays.asList(0, 0, 0));
            }
        });

        this.log(
                String.format(
                        "[%s] Game %s on map %s has started!",
                        timestamp.format(new Date()),
                        Integer.toHexString(this.game.hashCode()),
                        this.game.getMap().getName()
                )
        );

        this.log(
                String.format(
                        "[%s] Teams RED: %s BLUE: %s",
                        timestamp.format(new Date()),
                        Arrays.toString(PlayerHelper.usernameArray(this.game.getTeam(Team.RED))),
                        Arrays.toString(PlayerHelper.usernameArray(this.game.getTeam(Team.BLUE)))
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameKill(GameKillEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        String killer = event.getKiller().getName();
        String killed = event.getKilled().getName();
        String action = event.getType().name();
        String killerCharacter = PlayerHelper.getSelectedCharacter(event.getKiller().getUniqueId()).getName();

        this.log(
                String.format(
                        "[%s] %s (%s - %s) killed %s (%s - %s) while %s",
                        timestamp.format(new Date()),
                        killer, PlayerHelper.getTeam(event.getKiller()).name(), killerCharacter,
                        killed, PlayerHelper.getTeam(event.getKilled()).name(),
                        PlayerHelper.getSelectedCharacter(event.getKilled().getUniqueId()).getName(),
                        action
                )
        );

        this.kills.get(killerCharacter).set(event.getType().index, this.kills.get(killerCharacter).get(event.getType().index) + 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePointCap(GamePointCapEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        Team winner = event.getCaptured();
        ControlPoint point = this.game.getMap().getPoints()[winner.getOpponent().getIndex()];
        List<UUID>[] captured = point.getPlayersOnPoint(game);
        int attackers = captured[winner.getIndex()].size();
        int defenders = captured[winner.getOpponent().getIndex()].size();

        this.log(
                String.format(
                        "[%s] Team %s captured the point with %s attackers and %s defenders",
                        timestamp.format(new Date()),
                        winner.name(), attackers, defenders
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameFinish(GameFinishEvent event) {
        if (!event.isGame(this.game)) {
            return;
        }

        this.log(
                String.format(
                        "[%s] Team %s won the game!",
                        timestamp.format(new Date()),
                        event.getWinner().name()
                )
        );

        this.kills.forEach((character, kills) -> SheetsAPI.setKills(SheetsAPI.connect(), character, kills.get(0), kills.get(1), kills.get(2)));
    }

    public void log(String message) {
        try {
            this.writer.write(message);
            this.writer.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(message);
    }
}
