package com.battimod.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.*;

import com.battimod.team.TeamManager;

public class CommandForceInfo {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("forceinfo")
                    .then(CommandManager.argument("query", StringArgumentType.word())
                            .executes(ctx -> {
                                String input = StringArgumentType.getString(ctx, "query");
                                ServerCommandSource source = ctx.getSource();

                                // Check if input is a team
                                if (input.equalsIgnoreCase("rot") || input.equalsIgnoreCase("blau")) {
                                    List<UUID> uuids = TeamManager.getTeams().get(input.toLowerCase());
                                    if (uuids == null || uuids.isEmpty()) {
                                        source.sendMessage(colored("Team " + input + " hat keine Spieler.", input));
                                    } else {
                                        source.sendMessage(colored("Team " + input + " Spieler:", input));
                                        for (UUID uuid : uuids) {
                                            ServerPlayerEntity p = source.getServer().getPlayerManager().getPlayer(uuid);
                                            if (p != null) {
                                                source.sendMessage(Text.of(" - " + p.getName().getString()));
                                            }
                                        }
                                    }
                                } else {
                                    // Try to resolve player
                                    ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(input);
                                    if (target == null) {
                                        source.sendError(Text.of("Spieler '" + input + "' wurde nicht gefunden."));
                                        return 1;
                                    }

                                    String team = TeamManager.getPlayerTeam(target);
                                    if (team == null) {
                                        source.sendMessage(Text.of(target.getName().getString() + " ist in keinem Team."));
                                    } else {
                                        source.sendMessage(colored(target.getName().getString() + " ist in Team " + team + ".", team));
                                    }
                                }

                                return 1;
                            })));
        });
    }

    private static Text colored(String msg, String team) {
        Formatting color = team.equalsIgnoreCase("rot") ? Formatting.RED : Formatting.BLUE;
        return Text.literal(msg).setStyle(Style.EMPTY.withColor(color));
    }
}
