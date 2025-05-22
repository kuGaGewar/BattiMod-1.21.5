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
                            .then(literal("set")
                                    .then(argument("sekunden", IntegerArgumentType.integer(1))
                                            .executes(ctx -> {
                                                int sek = IntegerArgumentType.getInteger(ctx, "sekunden");
                                                GameSettings.gameSeconds = sek;
                                                ctx.getSource().sendFeedback(() -> Text.literal("⏱️ Spielzeit auf " + sek + " Sekunden gesetzt."), false);
                                                return 1;
                                            })
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
