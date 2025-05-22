package com.battimod.network;

import com.battimod.client.ClientTeamState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class TeamSyncPacket {
    // Registrierung des Payload-Typs
    public static void registerPayloadClientOnly() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C()
                .register(TeamSyncPayload.PACKET_ID, TeamSyncPayload.CODEC);
    }


    // Senden vom Server an den Client
    public static void send(ServerPlayerEntity player, String team) {
        TeamSyncPayload payload = new TeamSyncPayload(team);
        ServerPlayNetworking.send(player, payload);
    }

    // Registrierung des Handlers auf dem Client
    public static void registerClientHandler() {
        ClientPlayNetworking.registerGlobalReceiver(TeamSyncPayload.PACKET_ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientTeamState.setClientTeam(payload.team());
            });
        });
    }
}
