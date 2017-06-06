package me.koenn.kingdomwars.logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.koenn.kingdomwars.game.Game;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Copyright (C) Koenn - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Koen Willemse, May 2017
 */
public final class EventLogger implements HttpHandler {

    private static final List<JSONObject> events = new ArrayList<>();
    private static final JSONObject liveOverview = new JSONObject();

    private final HttpServer server;

    public EventLogger() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/stats", this);
        server.start();
        liveOverview.put("other", new JSONObject());
        log(new Message("info", "EventLogger enabled!"));
    }

    public static void log(JSONObject message) {
        JSONObject log = new JSONObject();
        log.put("timestamp", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        log.put("value", message);
        events.add(log);

        String game = (String) message.get("_game");
        JSONObject jsonObject = new JSONObject();
        if (game != null && liveOverview.containsKey(game)) {
            jsonObject = (JSONObject) liveOverview.get(game);
        }

        for (Object key : message.keySet()) {
            if (((String) key).startsWith("_")) {
                continue;
            }
            if (key.equals("info")) {
                JSONArray infoLog = ((JSONObject) liveOverview.get("other")).get("info") == null ? new JSONArray() : (JSONArray) ((JSONObject) liveOverview.get("other")).get("info");
                infoLog.add("[" + log.get("timestamp") + "]: " + message.get(key));
                if (infoLog.size() == 6) {
                    infoLog.remove(0);
                }
                jsonObject.put("info", infoLog);
                continue;
            }
            jsonObject.put(key, message.get(key));
        }

        liveOverview.put(game == null ? "other" : game, jsonObject);
    }

    public static void log(Game game, Message message) {
        String gameId = Integer.toHexString(game.hashCode());
        JSONObject messageObj = new JSONObject();
        messageObj.put("_game", gameId);
        for (Object key : message.keySet()) {
            messageObj.put(key, message.get(key));
        }
        log(messageObj);
    }

    public void disable() {
        this.server.stop(0);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = new JsonParser().parse(liveOverview.toJSONString());
        String response = "<!DOCTYPE html>\n<html><body><pre style=\"word-wrap: break-word; white-space: pre-wrap;\">" + gson.toJson(je) + "</pre></body><script type=\"text/javascript\">\nsetTimeout(function(){\nlocation = ''\n},1000)\n</script></html>";
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
