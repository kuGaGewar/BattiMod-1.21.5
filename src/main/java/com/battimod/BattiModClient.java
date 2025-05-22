package com.battimod;

import com.battimod.client.GameHUDOverlay;
import com.battimod.network.TeamSyncPacket;
import net.fabricmc.api.ClientModInitializer;

public class BattiModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 🎮 HUD anzeigen
        GameHUDOverlay.register();

        // 🔌 Netzwerk-Handler (Client-seitig)
        TeamSyncPacket.registerPayloadClientOnly();


        TeamSyncPacket.registerClientHandler();
    }
}
