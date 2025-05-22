package com.battimod.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TeamSyncPayload(String team) implements CustomPayload {
    public static final Identifier ID = Identifier.tryParse("battimod:team_sync");
    public static final CustomPayload.Id<TeamSyncPayload> PACKET_ID = new CustomPayload.Id<>(ID);
    public static final PacketCodec<RegistryByteBuf, TeamSyncPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.STRING, TeamSyncPayload::team, TeamSyncPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
