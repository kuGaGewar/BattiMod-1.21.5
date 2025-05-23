package com.battimod;

import com.battimod.commands.*;
import com.battimod.game.GameManager;
import com.battimod.network.TeamSyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class BattiMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// 🧠 Serverseitige Befehle registrieren
		CommandForceJoin.register();
		CommandForceStart.register();
		CommandForceInfo.register();
		CommandForceCountdown.register();
		CommandForceTimer.register();
		CommandForceLeave.register();
		CommandForceResults.register();



		// ⏱️ GameManager starten, wenn Server hochfährt, wichtig
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			GameManager.init(server);
		});
	}
}
