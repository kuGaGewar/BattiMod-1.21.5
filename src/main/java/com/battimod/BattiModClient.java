package com.battimod;

import com.battimod.client.ClientItemTargetState;
import com.battimod.client.GameHUDOverlay;
import com.battimod.network.ItemTargetPayload;
import com.battimod.network.TeamSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class BattiModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        // 🎮 HUD anzeigen
        GameHUDOverlay.register();

        // 🔌 Netzwerk-Handler (Client-seitig)
        TeamSyncPacket.registerPayloadClientOnly();
        TeamSyncPacket.registerClientHandler();

        //Für ItemTargetPayload
        PayloadTypeRegistry.playS2C().register(ItemTargetPayload.PACKET_ID, ItemTargetPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ItemTargetPayload.PACKET_ID, (payload, context) -> {
            ClientItemTargetState.setTarget(payload.item());
        });

    }
}
