package com.battimod.game;

import com.battimod.network.ItemTargetPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class ItemBattleManager {

    private static final Random random = new Random();

    // Spieler → aktuelles Ziel-Item
    private static final Map<UUID, Item> currentTargets = new HashMap<>();

    // Spieler → erledigte Items
    private static final Map<UUID, List<Item>> completedItems = new HashMap<>();

    // Spieler → Punktestand
    private static final Map<UUID, Integer> scores = new HashMap<>();

    public static void startBattle(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            giveNewTarget(player);
            completedItems.put(player.getUuid(), new ArrayList<>());
            scores.put(player.getUuid(), 0);
        }
    }

    public static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            Item target = currentTargets.get(uuid);
            if (target == null) continue;

            // Prüfe, ob Spieler das Item hat
            if (player.getInventory().contains(new ItemStack(target))) {
                // Punkt vergeben
                scores.put(uuid, scores.get(uuid) + 1);
                completedItems.get(uuid).add(target);
                giveNewTarget(player);
            }
        }
    }

    public static void giveNewTarget(ServerPlayerEntity player) {
        List<Item> allItems = Registries.ITEM.stream().toList();
        Item randomItem = allItems.get(random.nextInt(allItems.size()));
        currentTargets.put(player.getUuid(), randomItem);

        //An Client senden
        ServerPlayNetworking.send(player, new ItemTargetPayload(randomItem));

    }

    public static Item getCurrentTarget(ServerPlayerEntity player) {
        return currentTargets.get(player.getUuid());
    }

    public static int getScore(ServerPlayerEntity player) {
        return scores.getOrDefault(player.getUuid(), 0);
    }

    public static List<Item> getCompletedItems(ServerPlayerEntity player) {
        return completedItems.getOrDefault(player.getUuid(), List.of());
    }

    public static Map<UUID, Integer> getScores() {
        return scores;
    }

    public static List<Item> getCompletedItems(UUID uuid) {
        return completedItems.getOrDefault(uuid, List.of());
    }



}
