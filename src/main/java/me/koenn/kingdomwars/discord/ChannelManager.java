package me.koenn.kingdomwars.discord;

import me.koenn.kingdomwars.game.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ChannelManager {

    public static final HashMap<Game, Lobby> gameLobbies = new HashMap<>();
    private static final List<Lobby> lobbies = new ArrayList<>();

    public static void loadChannels() {
        lobbies.add(new Lobby(
                "404634723479060490",
                "404582461356638208",
                "404582401558183936"
        ));
        lobbies.add(new Lobby(
                "404634856019197972",
                "404634489789218829",
                "404634562493153281"
        ));
        lobbies.add(new Lobby(
                "404634845029990401",
                "404634523695841301",
                "404634579371163649"
        ));
        lobbies.add(new Lobby(
                "404634836276346881",
                "404634543732031500",
                "404634571431477249"
        ));
    }

    public static void registerGame(Game game) {
        Lobby lobby = lobbies.get(0);
        lobbies.remove(lobby);

        gameLobbies.put(game, lobby);
    }
}
