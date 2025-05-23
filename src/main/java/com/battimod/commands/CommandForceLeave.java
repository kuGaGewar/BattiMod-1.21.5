package com.battimod.commands;

import com.battimod.team.TeamManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandForceLeave {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("forceleave")
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                TeamManager.removePlayerFromTeams(player.getUuid());
                                ctx.getSource().sendFeedback(() -> Text.literal("🚪 Du hast dein Team verlassen."), false);
                                return 1;
                            })
            );
        });
    }
}
