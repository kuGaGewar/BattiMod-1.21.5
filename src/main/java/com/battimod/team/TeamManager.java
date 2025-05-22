package com.battimod.team;

import com.battimod.network.TeamSyncPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.*;

public class TeamManager {
    private static final Map<String, List<UUID>> teams = new HashMap<>();

    public static void addToTeam(String team, ServerPlayerEntity player) {
        teams.computeIfAbsent(team.toLowerCase(), t -> new ArrayList<>()).add(player.getUuid());
        player.sendMessage(colored("Du bist Team " + capitalize(team) + " beigetreten!", team), false);

        if (player.getServer() != null && player.getServer().isDedicated()) {
            com.battimod.network.TeamSyncPacket.send(player, team.toLowerCase());
        }

        TeamSyncPacket.send(player, team.toLowerCase());

        // Entferne Spieler aus allen anderen Teams
        teams.forEach((otherTeam, list) -> {
            if (!otherTeam.equals(team)) {
                list.remove(player.getUuid()); //vorgeschlagen wurde list.remove(playerUuid) aber oben steht was anderes ?!
            }
        });


    }

    public static boolean isInTeam(ServerPlayerEntity player, String team) {
        List<UUID> members = teams.getOrDefault(team.toLowerCase(), Collections.emptyList());
        return members.contains(player.getUuid());
    }

    public static Map<String, List<UUID>> getTeams() {
        return teams;
    }


    public static String getPlayerTeam(ServerPlayerEntity player) {
        for (Map.Entry<String, List<UUID>> entry : teams.entrySet()) {
            if (entry.getValue().contains(player.getUuid())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static Text colored(String msg, String team) {
        Formatting color = team.equalsIgnoreCase("rot") ? Formatting.RED : Formatting.BLUE;
        return Text.literal(msg).setStyle(Style.EMPTY.withColor(color));
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }


}