package com.battimod.game;

import com.battimod.network.ItemTargetPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

        //Hier die Liste mit den verbotenen Items
        List<Item> allItems = Registries.ITEM.stream()
                .filter(item -> item.getMaxCount() > 0)
                .filter(item -> !item.getTranslationKey().contains("spawn_egg"))
                .filter(item -> !item.getTranslationKey().contains("banner_pattern"))
                .filter(item -> !item.equals(Items.ENCHANTED_BOOK))
                .filter(item -> !item.equals(Items.BEDROCK))
                .filter(item -> !item.equals(Items.BARRIER))
                .filter(item -> !item.equals(Items.COMMAND_BLOCK))
                .filter(item -> !item.equals(Items.CHAIN_COMMAND_BLOCK))
                .filter(item -> !item.equals(Items.REPEATING_COMMAND_BLOCK))
                .filter(item -> !item.equals(Items.COMMAND_BLOCK_MINECART))
                .filter(item -> !item.equals(Items.DEBUG_STICK))
                .filter(item -> !item.equals(Items.STRUCTURE_BLOCK))
                .filter(item -> !item.equals(Items.STRUCTURE_VOID))
                .filter(item -> !item.equals(Items.JIGSAW))
                .filter(item -> !item.equals(Items.KNOWLEDGE_BOOK))
                .toList();


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

    private static final Map<UUID, Integer> jokerCounts = new HashMap<>();

    public static void giveJokers(UUID player, int count) {
        jokerCounts.put(player, jokerCounts.getOrDefault(player, 0) + count);
    }

    public static boolean useJoker(UUID player) {
        int current = jokerCounts.getOrDefault(player, 0);
        if (current > 0) {
            jokerCounts.put(player, current - 1);
            return true;
        }
        return false;
    }

    public static int getJokerCount(UUID player) {
        return jokerCounts.getOrDefault(player, 0);
    }


}
