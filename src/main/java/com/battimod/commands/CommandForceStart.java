package com.battimod.commands;


import com.battimod.GameSettings;
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
                        player.sendMessage(Text.of("⏳ Macht euch bereit!"), false);


                        GameManager.startCountdown(); // Dieser startet beides automatisch in deinem GameManager-Tick

                        return 1;
                    }));
        });
    }
}
