package com.battimod.commands;

import com.battimod.game.GameManager;
import com.battimod.team.TeamManager;


import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;


public class CommandForceJoin {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("forcejoin")
                    .then(CommandManager.argument("team", StringArgumentType.word())
                            .executes(ctx -> {
                                if (GameManager.isGameRunning() || GameManager.isGamePaused()) {
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("❌ Teamwechsel während des Spiels nicht erlaubt."), false);
                                    return 0;
                                }
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                String team = StringArgumentType.getString(ctx, "team");
                                return assignTeam(player, team, ctx.getSource());
                            })
                            .then(CommandManager.argument("spieler", EntityArgumentType.player())
                                    .requires(source -> source.hasPermissionLevel(2)) // Nur Admins/OPs
                                    .executes(ctx -> {
                                        if (GameManager.isGameRunning() || GameManager.isGamePaused()) {
                                            ctx.getSource().sendFeedback(() ->
                                                    Text.literal("❌ Teamwechsel während des Spiels nicht erlaubt."), false);
                                            return 0;
                                        }
                                        String team = StringArgumentType.getString(ctx, "team");
                                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "spieler");
                                        return assignTeam(target, team, ctx.getSource());
                                    }))));
        });
    }

    private static int assignTeam(ServerPlayerEntity player, String team, ServerCommandSource source) {
        if (team.equalsIgnoreCase("rot") || team.equalsIgnoreCase("blau")) {
            TeamManager.addToTeam(team, player);
            source.sendFeedback(() -> Text.of(player.getName().getString() + " wurde Team " + team + " zugewiesen."), false);
        } else {
            source.sendError(Text.of("Ungültiges Team: " + team));
        }
        return 1;
    }
}