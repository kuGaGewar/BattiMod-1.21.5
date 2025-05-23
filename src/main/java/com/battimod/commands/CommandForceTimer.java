package com.battimod.commands;

import com.battimod.GameSettings;
import com.battimod.game.GameManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandForceTimer {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("forcetimer")
                            .then(literal("set_hms")
                                    .then(argument("stunden", IntegerArgumentType.integer(0))
                                            .then(argument("minuten", IntegerArgumentType.integer(0, 59))
                                                    .then(argument("sekunden", IntegerArgumentType.integer(0, 59))
                                                            .executes(ctx -> {
                                                                int h = IntegerArgumentType.getInteger(ctx, "stunden");
                                                                int m = IntegerArgumentType.getInteger(ctx, "minuten");
                                                                int s = IntegerArgumentType.getInteger(ctx, "sekunden");
                                                                int total = h * 3600 + m * 60 + s;

                                                                GameSettings.gameSeconds = total;
                                                                GameManager.setGameSeconds(total);
                                                                ctx.getSource().sendFeedback(() -> Text.literal("⏱️ Zeit gesetzt auf " + h + "h " + m + "m " + s + "s"), false);
                                                                return 1;
                                                            })
                                                    )
                                            )
                                    )
                            )

                            .then(literal("pause")
                                    .executes(ctx -> {
                                        if (!GameManager.isGameRunning()) {
                                            ctx.getSource().sendFeedback(() -> Text.literal("❌ Kein aktives Spiel."), false);
                                            return 0;
                                        }
                                        GameManager.pauseGame();
                                        ctx.getSource().sendFeedback(() -> Text.literal("⏸️ Spielzeit pausiert."), false);
                                        return 1;
                                    })
                            )
                            .then(literal("resume")
                                    .executes(ctx -> {
                                        if (!GameManager.isGameRunning()) {
                                            ctx.getSource().sendFeedback(() -> Text.literal("❌ Kein aktives Spiel."), false);
                                            return 0;
                                        }
                                        GameManager.resumeGame();
                                        ctx.getSource().sendFeedback(() -> Text.literal("▶️ Spielzeit fortgesetzt."), false);
                                        return 1;
                                    })
                            )
            );
        });
    }
}
