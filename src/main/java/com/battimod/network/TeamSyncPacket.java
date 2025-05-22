package com.battimod.network;

import com.battimod.client.ClientTeamState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import io.netty.buffer.Unpooled;

public class TeamSyncPacket {

    public static final Identifier ID = new Identifier("battimod:team_sync");

    public static void sendToClient(ServerPlayerEntity player, String team) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(team, 64);

        CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new CustomPayload() {
            @Override
            public Identifier id() {
                return ID;
            }

            @Override
            public void write(PacketByteBuf output) {
                output.writeString(team, 64);
            }
        });

        player.networkHandler.sendPacket(packet);
    }

    public static void registerClientReceiver() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            handler.getConnection().setPacketListener((packet) -> {
                if (packet instanceof CustomPayloadS2CPacket customPacket &&
                        customPacket.payload().id().equals(ID)) {

                    PacketByteBuf buf = new PacketByteBuf(Unpooled.wrappedBuffer(customPacket.payload().write(PacketByteBufs.create()).array()));
                    String team = buf.readString(64);
                    client.execute(() -> ClientTeamState.setClientTeam(team));
                }
            });
        });
    }
}
