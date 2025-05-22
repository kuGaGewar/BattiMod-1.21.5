package com.battimod.commands;

import com.battimod.GameSettings;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandForceCountdown {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register(
                    literal("forcecountdown")
                            .then(literal("set")
                                    .then(argument("sekunden", IntegerArgumentType.integer(1))
                                            .executes(ctx -> {
                                                int sek = IntegerArgumentType.getInteger(ctx, "sekunden");
                                                GameSettings.countdownSeconds = sek;
                                                ctx.getSource().sendFeedback(() -> Text.literal("⏳ Countdown auf " + sek + " Sekunden gesetzt."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}
