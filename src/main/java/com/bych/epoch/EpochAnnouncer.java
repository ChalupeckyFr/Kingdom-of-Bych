package com.bych.epoch;

import com.bych.BychRuntime;
import com.bych.config.BychConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Říká hráčům v chatu, který je den a která doba.
 * Při vstupu do světa to řekne jen tomu, kdo přišel.
 * Když se den nebo doba změní, řekne to všem, ale jen jednou.
 */
public final class EpochAnnouncer {
	private int announcedDay = -1;
	private Epoch announcedEpoch;

	public Component describe(ServerLevel overworld) {
		BychRuntime runtime = BychRuntime.get();
		int day = runtime.clock().currentDay(overworld);
		Epoch epoch = runtime.clock().epochForDay(day, runtime.config());
		return Component.literal(epoch.czechName() + ", den " + day + ".");
	}

	public void tellJoiningPlayer(ServerPlayer player) {
		ServerLevel overworld = player.getServer().overworld();
		this.remember(overworld);
		player.sendSystemMessage(this.describe(overworld));
	}

	/**
	 * Zavolá se každý krok hlavního světa.
	 * První krok si den jen zapamatuje, ať se vstup do světa neohlásí dvakrát.
	 * Další změnu dne nebo doby už řekne všem.
	 */
	public void onOverworldTick(MinecraftServer server) {
		ServerLevel overworld = server.overworld();
		int day = BychRuntime.get().clock().currentDay(overworld);
		Epoch epoch = BychRuntime.get().clock().epochForDay(day, BychRuntime.get().config());
		if (this.announcedDay == -1) {
			this.announcedDay = day;
			this.announcedEpoch = epoch;
			return;
		}
		if (day == this.announcedDay && epoch == this.announcedEpoch) {
			return;
		}
		this.announcedDay = day;
		this.announcedEpoch = epoch;
		if (server.getPlayerCount() == 0) {
			return;
		}
		this.tellEveryone(server, this.describe(overworld));
	}

	/** Po příkazu /kbych setday řekne nový den i tehdy, když už stejný den jednou zazněl. */
	public void tellAfterSetDay(MinecraftServer server) {
		ServerLevel overworld = server.overworld();
		this.remember(overworld);
		this.tellEveryone(server, this.describe(overworld));
	}

	private void remember(ServerLevel overworld) {
		BychConfig config = BychRuntime.get().config();
		this.announcedDay = BychRuntime.get().clock().currentDay(overworld);
		this.announcedEpoch = BychRuntime.get().clock().epochForDay(this.announcedDay, config);
	}

	private void tellEveryone(MinecraftServer server, Component text) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.sendSystemMessage(text);
		}
	}
}
