package com.battimod.commands;

import com.battimod.GameSettings;
import com.battimod.game.GameManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class CommandForceCountdown {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("forcecountdown")
                    .then(CommandManager.argument("sekunden", IntegerArgumentType.integer(1, 60))
                            .requires(src -> src.hasPermissionLevel(2))
                            .executes(ctx -> {
                                int sek = IntegerArgumentType.getInteger(ctx, "sekunden");
                                //GameManager.setCountdownSeconds(sek); Auskommentierung verhindert den automatischen Start des Countdowns
                                GameSettings.countdownSeconds = sek;
                                ctx.getSource().sendFeedback(() -> Text.of("Countdown gesetzt auf " + sek + " Sekunden."), false);
                                return 1;
                            })));
        });
    }
}
