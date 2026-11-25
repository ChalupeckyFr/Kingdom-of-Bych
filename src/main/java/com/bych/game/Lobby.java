package com.bych.game;

import com.bych.BychRuntime;
import com.bych.KingdomOfBych;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;

/**
 * Lobby na začátku hry: malý world border kolem spawnu.
 * Odlet zatím jen ohlásí a nastaví den 1. Let autobusu přijde později.
 */
public final class Lobby {
	/** Poloměr lobby ve blocích od středu. Průměr world borderu je dvojnásobek. */
	public static final double RADIUS_BLOCKS = 32.0;

	private static boolean departed;

	private Lobby() {
	}

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(Lobby::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> departed = false);
	}

	private static void onServerStarted(MinecraftServer server) {
		if (departed) {
			return;
		}
		applyLobbyBorder(server.overworld());
	}

	/**
	 * Nastaví střed na spawn a omezí svět na asi 32 bloků od středu.
	 */
	public static void applyLobbyBorder(ServerLevel overworld) {
		WorldBorder border = overworld.getWorldBorder();
		BlockPos spawn = overworld.getSharedSpawnPos();
		border.setCenter(spawn.getX() + 0.5, spawn.getZ() + 0.5);
		border.setSize(RADIUS_BLOCKS * 2.0);
		KingdomOfBych.LOGGER.info(
				"Lobby: world border kolem spawnu, poloměr {} bloků.",
				RADIUS_BLOCKS
		);
	}

	/**
	 * Odlet bez letu: česká hláška, konec lobby a den 1 jako začátek hry.
	 */
	public static void depart(MinecraftServer server) {
		ServerLevel overworld = server.overworld();
		Component odlet = Component.literal("Odlet!");
		tellEveryone(server, odlet);
		KingdomOfBych.LOGGER.info(odlet.getString());

		liftBorder(overworld);
		departed = true;

		BychRuntime.get().clock().applyDay(overworld, 1);
		BychRuntime.get().announcer().tellAfterSetDay(server);
	}

	/** Uvolní world border, ať hráči po odletu nejsou v kleci. */
	public static void liftBorder(ServerLevel overworld) {
		WorldBorder border = overworld.getWorldBorder();
		border.setSize(WorldBorder.MAX_SIZE);
	}

	public static boolean hasDeparted() {
		return departed;
	}

	private static void tellEveryone(MinecraftServer server, Component text) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.sendSystemMessage(text);
		}
	}
}
