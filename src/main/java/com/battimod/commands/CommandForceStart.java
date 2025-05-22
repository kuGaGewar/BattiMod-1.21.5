package com.battimod.commands;


import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import com.battimod.game.GameManager;

public class CommandForceStart {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("forcestart")
                    .requires(source -> source.hasPermissionLevel(2)) // Nur OP
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                        player.sendMessage(Text.of("⏳ Spiel-Countdown gestartet."), false);
                        GameManager.startCountdown();
                        return 1;
                    }));
        });
    }
}
