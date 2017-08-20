package me.koenn.kingdomwars.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.koenn.core.data.JSONManager;
import me.koenn.kingdomwars.KingdomWars;
import me.koenn.kingdomwars.logger.EventLogger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential Written by Koen Willemse, June 2017
 */
public class StatsManager implements HttpHandler, Runnable {

    public static final HashMap<UUID, PlayerStats> stats = new HashMap<>();
    private JSONManager jsonManager;

    public StatsManager(String fileName) {
        try {
            jsonManager = new JSONManager(KingdomWars.getInstance(), fileName);
            if (jsonManager.getFromBody("stats") == null) {
                jsonManager.setInBody("stats", new JSONObject());
            }

            final JSONObject stats = (JSONObject) jsonManager.getFromBody("stats");
            stats.keySet().forEach(key -> {
                final UUID uuid = UUID.fromString((String) key);
                StatsManager.stats.put(uuid, new PlayerStats(uuid, (JSONObject) stats.get(key)));
            });
        } catch (Exception ex) {
            KingdomWars.getInstance().getLogger().severe("Error while loading stats: " + ex);
            ex.printStackTrace();
        }

        try {
            EventLogger.server.createContext("/stats", this);
            KingdomWars.getInstance().getLogger().info("Created context");
        } catch (Exception ex) {
            KingdomWars.getInstance().getLogger().severe("Error while starting HttpServer: " + ex);
            ex.printStackTrace();
        }
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    public void save() {
        final JSONObject statsObject = new JSONObject();
        stats.keySet().forEach(uuid -> statsObject.put(uuid, stats.get(uuid).toJSON()));
        jsonManager.setInBody("stats", statsObject);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> params = queryToMap(httpExchange.getRequestURI().getQuery());
        JsonElement je = new JsonParser().parse(stats.get(UUID.fromString(params.get("uuid"))).toJSON().toJSONString());
        String response = "<!DOCTYPE html>\n<html><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">" + gson.toJson(je) + "</pre></body><script type=\"text/javascript\">\nsetTimeout(function(){\nlocation = ''\n},1000)\n</script></html>";
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    @Override
    public void run() {
        this.save();
    }
}
