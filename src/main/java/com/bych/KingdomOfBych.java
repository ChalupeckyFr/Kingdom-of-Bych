package com.bych;

import com.bych.command.BychCommands;
import com.bych.config.BychConfig;
import com.bych.sleep.SleepTracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KingdomOfBych implements ModInitializer {
	public static final String MOD_ID = "kingdom-of-bych";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BychConfig config = BychConfig.loadOrCreate();
		BychRuntime.start(config);
		LOGGER.info(config.summary().getString());

		BychCommands.register();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> BychRuntime.get().applySleepRule(server));
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> BychRuntime.get().clearSleepers());
		ServerTickEvents.END_WORLD_TICK.register(level -> BychRuntime.get().onWorldTick(level));

		// Lehnutí a vstávání hlídáme jen u skutečného hráče na serveru.
		// Klientská postava tu nemá co dělat, rozhoduje server.
		EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayer player) {
				SleepTracker tracker = BychRuntime.get().sleepTracker();
				tracker.start(player, BychRuntime.get().config().requiredSleepSeconds());
			}
		});
		EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
			if (entity instanceof ServerPlayer player) {
				BychRuntime.get().sleepTracker().forget(player);
			}
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				BychRuntime.get().announcer().tellJoiningPlayer(handler.getPlayer())
		);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
				BychRuntime.get().sleepTracker().forget(handler.getPlayer())
		);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
