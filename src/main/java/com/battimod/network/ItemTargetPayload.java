package com.battimod.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record ItemTargetPayload(Item item) implements CustomPayload {

    public static final Identifier ID = Identifier.tryParse("battimod:item_target");
    public static final CustomPayload.Id<ItemTargetPayload> PACKET_ID = new CustomPayload.Id<>(ID);

    public static final PacketCodec<RegistryByteBuf, ItemTargetPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.registryValue(RegistryKeys.ITEM),
                    ItemTargetPayload::item,
                    ItemTargetPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
