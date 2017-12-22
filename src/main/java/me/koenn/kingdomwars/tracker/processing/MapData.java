package me.koenn.kingdomwars.tracker.processing;

import me.koenn.kingdomwars.game.map.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MapData implements DataStructure {

    private final Map map;

    public int totalMatches;
    public int[] totalCaptures;
    public int[] totalDefenses;
    public int[] totalKills;
    public int avgMatchTime;

    public MapData(Map map) {
        this.map = map;

        this.totalCaptures = new int[2];
        this.totalDefenses = new int[2];
        this.totalKills = new int[2];
    }

    public MapData(JSONObject json) {
        this.map = Map.getMap((String) json.get("map"));
        this.totalCaptures = DataHelper.jsonToIntArray((JSONArray) json.get("totalCaptures"));
        this.totalDefenses = DataHelper.jsonToIntArray((JSONArray) json.get("totalDefenses"));
        this.totalKills = DataHelper.jsonToIntArray((JSONArray) json.get("totalKills"));
    }

    @Override
    public String getName() {
        return this.map.getName();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("map", this.map.getName());
        json.put("totalCaptures", DataHelper.intToJSONArray(this.totalCaptures));
        json.put("totalDefenses", DataHelper.intToJSONArray(this.totalDefenses));
        json.put("totalKills", DataHelper.intToJSONArray(this.totalKills));
        json.put("avgMatchTime", this.avgMatchTime);
        return json;
    }
}
