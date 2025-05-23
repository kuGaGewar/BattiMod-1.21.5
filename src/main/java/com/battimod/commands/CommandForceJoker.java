package com.battimod.commands;

import com.battimod.game.ItemBattleManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandForceJoker {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("forcejoker")

                            // /forcejoker <anzahl>
                            .then(argument("anzahl", IntegerArgumentType.integer(1))
                                    .executes(ctx -> {
                                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                                        int anzahl = IntegerArgumentType.getInteger(ctx, "anzahl");

                                        giveJokerTo(player, anzahl);

                                        ctx.getSource().sendFeedback(() ->
                                                Text.literal("🃏 Du hast " + anzahl + " Joker erhalten."), false);
                                        return 1;
                                    })
                            )

                            // /forcejoker give <spieler> <anzahl>
                            .then(literal("give")
                                    .then(argument("spieler", StringArgumentType.word())
                                            .then(argument("anzahl", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        String spielerName = StringArgumentType.getString(ctx, "spieler");
                                                        int anzahl = IntegerArgumentType.getInteger(ctx, "anzahl");

                                                        ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(spielerName);
                                                        if (target == null) {
                                                            ctx.getSource().sendFeedback(() -> Text.literal("❌ Spieler nicht gefunden."), false);
                                                            return 0;
                                                        }

                                                        giveJokerTo(target, anzahl);

                                                        ctx.getSource().sendFeedback(() ->
                                                                Text.literal("🃏 " + anzahl + " Joker an " + target.getName().getString() + " gegeben."), false);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(literal("set")
                                    .then(argument("anzahl", IntegerArgumentType.integer(0))
                                            .executes(ctx -> {
                                                int anzahl = IntegerArgumentType.getInteger(ctx, "anzahl");
                                                com.battimod.GameSettings.jokerCount = anzahl;

                                                ctx.getSource().sendFeedback(() ->
                                                        Text.literal("🔧 Startanzahl Joker auf " + anzahl + " gesetzt."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }

    public static void giveJokerTo(ServerPlayerEntity player, int anzahl) {
        ItemBattleManager.giveJokers(player.getUuid(), anzahl);

        ItemStack jokerItem = new ItemStack(Items.BARRIER, anzahl);
        jokerItem.set(DataComponentTypes.CUSTOM_NAME, Text.literal("🃏 Joker"));
        jokerItem.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.literal("Rechtsklick, um dein Ziel-Item zu erhalten.")
        )));

        player.giveItemStack(jokerItem);
    }
}
