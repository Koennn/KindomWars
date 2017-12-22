package me.koenn.kingdomwars.tracker.processing;

import org.json.simple.JSONObject;

import java.util.UUID;

public class PlayerData implements DataStructure {

    private final UUID player;

    public int matchesPlayed;
    public int totalKills;
    public int totalDeaths;
    public int totalWins;
    public int totalLosses;
    public int totalCaptures;

    public PlayerData(UUID player) {
        this.player = player;
    }

    public PlayerData(JSONObject json) {
        this.player = UUID.fromString((String) json.get("player"));
    }

    @Override
    public String getName() {
        return this.player.toString();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("player", this.player.toString());
        return json;
    }
}
