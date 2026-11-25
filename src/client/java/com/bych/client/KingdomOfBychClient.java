package com.bych.client;

import com.bych.client.furnace.FurnaceMouseChaos;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class KingdomOfBychClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		FurnaceMouseChaos.reloadClientConfig();
		// Chaos pece hlídáme na klientovi: citlivost a posun kliku patří do okna.
		ClientTickEvents.END_CLIENT_TICK.register(FurnaceMouseChaos::tick);
	}
}
