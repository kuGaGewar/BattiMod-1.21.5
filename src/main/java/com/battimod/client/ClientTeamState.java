package com.battimod.client;

public class ClientTeamState {
    private static String clientTeam = null;

    public static void setClientTeam(String team) {
        clientTeam = team;
    }

    public static String getClientTeam() {
        return clientTeam;
    }
}
