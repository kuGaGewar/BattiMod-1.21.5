package com.battimod.game;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.List;

import com.battimod.team.TeamManager;
import com.battimod.utils.ItemGiver;

public class GameManager {
    private static int countdownTicks = -1;
    private static MinecraftServer server;
    private static int gameTicks = -1;
    private static int maxGameTicks = -1;

    public static void init(MinecraftServer s) {
        server = s;
        ServerTickEvents.START_SERVER_TICK.register(GameManager::tick);
    }

    public static void startCountdown() {
        countdownTicks = 200; // 10 Sekunden bei 20 Ticks/Sekunde
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
            if (countdownTicks == 0) {
                startGame();
            }
        } else if (isGameRunning()) {
            gameTicks++;
            int secondsLeft = (maxGameTicks - gameTicks) / 20;
            if ((maxGameTicks - gameTicks) % 20 == 0 && secondsLeft <= 3 && secondsLeft > 0) {
                broadcast("Spiel endet in " + secondsLeft + "...");
                playCountdownSound(secondsLeft, server);
            }
            if (gameTicks >= maxGameTicks) {
                endGame();
            }
        }
    }

    private static void startGame() {
        broadcast("❗ Spiel startet jetzt!");
        TeamManager.getTeams().values().stream()
                .flatMap(List::stream)
                .map(uuid -> server.getPlayerManager().getPlayer(uuid))
                .filter(p -> p != null)
                .forEach(ItemGiver::giveRandomItem);
                gameTicks = 0;

    }

    private static void broadcast(String message) {
        if (server != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(Text.literal(message).formatted(Formatting.GOLD), false);
            }
        }
    }


    public static boolean isCountdownRunning() {
        return countdownTicks >= 0;
    }

    public static int getCountdownSeconds() {
        return countdownTicks / 20;
    }

    public static boolean isGameRunning() {
        return gameTicks >= 0;
    }

    public static String getFormattedTimer() {
        int seconds = gameTicks / 20;
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public static String getPlayerTeam(ClientPlayerEntity player) {
        return com.battimod.client.ClientTeamState.getClientTeam();
    }


    public static void setCountdownSeconds(int seconds) {
        countdownTicks = seconds * 20;
    }

    public static void setGameSeconds(int seconds) {
        maxGameTicks = seconds * 20;
        gameTicks = 0;
    }

    private static void playCountdownSound(int seconds, MinecraftServer server) {
        if (server == null) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerWorld world = (ServerWorld) player.getWorld();
            world.playSound(
                    null, // Kein spezifischer Spieler, der den Sound hört
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f + seconds * 0.1f
            );
        }
    }

    private static void endGame() {
        broadcast("❌ Das Spiel ist vorbei!");
        gameTicks = -1;
        maxGameTicks = -1;
    }


}