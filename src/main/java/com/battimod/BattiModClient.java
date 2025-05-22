package com.battimod;

import com.battimod.commands.*;
import com.battimod.game.GameManager;
import com.battimod.network.TeamSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import com.battimod.client.GameHUDOverlay;
import com.battimod.client.GameHUDOverlay;
import net.fabricmc.api.ClientModInitializer;

public class BattiModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CommandForceJoin.register(); // Für /forcejoin <team> [spieler]
        CommandForceStart.register(); // Für /forcestart
        CommandForceInfo.register();
        CommandForceCountdown.register();
        CommandForceTime.register();
        GameHUDOverlay.register();

        TeamSyncPacket.registerPayload();       // <--- Diese Zeile hinzufügen!
        TeamSyncPacket.registerClientHandler(); // Damit der Client auf Pakete reagiert

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            GameManager.init(server); // Startet Tick-Loop für Countdown
        });
    }
}
