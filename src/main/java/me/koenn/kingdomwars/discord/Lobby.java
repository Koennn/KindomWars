package me.koenn.kingdomwars.discord;

public class Lobby {

    private final String lobbyId;
    private final String blueId;
    private final String redId;

    public Lobby(String lobbyId, String blueId, String redId) {
        this.lobbyId = lobbyId;
        this.blueId = blueId;
        this.redId = redId;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public String getBlueId() {
        return blueId;
    }

    public String getRedId() {
        return redId;
    }
}
