package com.battimod.commands;

import com.battimod.game.ItemBattleManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandForceResults {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("forceresults")
                            .executes(ctx -> {
                                Map<UUID, Integer> scores = ItemBattleManager.getScores();
                                if (scores.isEmpty()) {
                                    ctx.getSource().sendFeedback(() -> Text.literal("❌ Noch keine Ergebnisse verfügbar."), false);
                                    return 0;
                                }

                                ctx.getSource().sendFeedback(() -> Text.literal("📊 Ergebnisse:"), false);

                                for (Map.Entry<UUID, Integer> entry : scores.entrySet()) {
                                    UUID uuid = entry.getKey();
                                    int score = entry.getValue();
                                    ServerPlayerEntity player = ctx.getSource().getServer().getPlayerManager().getPlayer(uuid);
                                    String name = (player != null) ? player.getName().getString() : uuid.toString();

                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("🔹 " + name + ": " + score + " Punkt(e)"), false);

                                    List<Item> items = ItemBattleManager.getCompletedItems(uuid);
                                    if (!items.isEmpty()) {
                                        String itemList = items.stream()
                                                .map(item -> item.getName().getString())
                                                .reduce((a, b) -> a + ", " + b)
                                                .orElse("");

                                        ctx.getSource().sendFeedback(() ->
                                                Text.literal("     📦 Items: " + itemList), false);
                                    }

                                }

                                return 1;
                            })
            );
        });
    }
}
