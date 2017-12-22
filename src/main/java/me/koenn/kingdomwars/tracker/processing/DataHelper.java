package me.koenn.kingdomwars.tracker.processing;

import org.json.simple.JSONArray;

public final class DataHelper {

    public static JSONArray intToJSONArray(int[] array) {
        JSONArray json = new JSONArray();
        for (int i : array) {
            json.add(i);
        }
        return json;
    }

    public static int[] jsonToIntArray(JSONArray json) {
        int[] array = new int[json.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) json.get(i);
        }
        return array;
    }
}
