package com.battimod.game;

import com.battimod.GameSettings;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.List;

import com.battimod.team.TeamManager;
import com.battimod.utils.ItemGiver;

public class GameManager {
    private static int countdownTicks = -1;
    private static MinecraftServer server;
    private static int gameTicks = -1;

    public static void init(MinecraftServer s) {
        server = s;
        ServerTickEvents.START_SERVER_TICK.register(GameManager::tick);
    }

    public static void startCountdown() {
        countdownTicks = GameSettings.countdownSeconds * 20;
    }

    public static boolean isCountdownRunning() {
        return countdownTicks >= 0;
    }

    public static boolean isGameRunning() {
        return gameTicks >= 0;
    }

    public static boolean isGameWaiting() {
        return gameTicks == -1 && countdownTicks == -1;
    }

    public static int getCountdownSeconds() {
        return countdownTicks / 20;
    }

    private static boolean gamePaused = false;

    public static void pauseGame() {
        gamePaused = true;
    }

    public static void resumeGame() {
        gamePaused = false;
    }

    public static boolean isGamePaused() {
        return gamePaused;
    }


    public static String getFormattedTimer() {
        int totalSeconds = gameTicks / 20;
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }


    public static String getPlayerTeam(ClientPlayerEntity player) {
        return com.battimod.client.ClientTeamState.getClientTeam();
    }

    public static void setCountdownSeconds(int seconds) {
        countdownTicks = seconds * 20;
    }

    public static void setGameSeconds(int seconds) {
        gameTicks = seconds * 20;
    }


    private static void tick(MinecraftServer server) {
        if (countdownTicks >= 0) {
            if (countdownTicks % 20 == 0) {
                int seconds = countdownTicks / 20;
                if (seconds <= 3) {
                    playCountdownSound(seconds, server);
                }
                broadcast("Start in " + seconds + "...");
            }

            countdownTicks--;

            if (countdownTicks <= 0) {
                startGame();
            }

        } else if (isGameRunning()) {
            if (isGamePaused()) return; // ⛔ Spielzeit pausiert, keine Aktion

            gameTicks--;

            ItemBattleManager.tick(server); // Wenn Game nicht pausiert ist soll durchgehend Inventar gecheckt werden ob momentanes Item vorhanden.


            if (gameTicks % 20 == 0 && gameTicks / 20 <= 3 && gameTicks / 20 > 0) {
                broadcast("Spiel endet in " + (gameTicks / 20) + "...");
                playCountdownSound(gameTicks / 20, server);
            }

            if (gameTicks <= 0) {
                endGame();
            }
        }
    }

    private static void startGame() {
        broadcast("❗ Spiel startet jetzt!");

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            com.battimod.commands.CommandForceJoker.giveJokerTo(player, GameSettings.jokerCount);
        }


        TeamManager.getTeams().values().stream()
                .flatMap(List::stream)
                .map(uuid -> server.getPlayerManager().getPlayer(uuid))
                .filter(p -> p != null)
                .forEach(ItemGiver::giveRandomItem);

        gameTicks = GameSettings.gameSeconds * 20;


        ItemBattleManager.startBattle(server); //startet das Battle
    }

    private static void endGame() {
        broadcast("❌ Das Spiel ist vorbei!");
        gameTicks = -1;
        countdownTicks = -1;
    }

    private static void broadcast(String message) {
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.literal(message).formatted(Formatting.GOLD), false);
            }
        }
    }

    private static void playCountdownSound(int seconds, MinecraftServer server) {
        if (server == null) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerWorld world = (ServerWorld) player.getWorld();
            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f + seconds * 0.1f
            );
        }
    }

}
