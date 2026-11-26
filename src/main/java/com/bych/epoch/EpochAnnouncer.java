package com.bych.epoch;

import com.bych.BychRuntime;
import com.bych.config.BychConfig;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * Hlídá přechod doby u každého hráče zvlášť.
 * Při změně ukáže velký nápis a zahraje zvuk. Loading screen a airdrop patří do dalších úkolů.
 */
public final class EpochAnnouncer {

	public Component describe(ServerPlayer player) {
		BychRuntime runtime = BychRuntime.get();
		PlaytimeStore store = PlaytimeStore.get(player.getServer());
		PlayerEpochData data = store.of(player.getUUID());
		BychConfig config = runtime.config();
		Epoch epoch = runtime.clock().epochForPlayTicks(data.playTicks(), config);
		if (data.medieval()) {
			return Component.literal("Středověk. Odehráno " + minutesPlayed(data.playTicks()) + " min.");
		}
		if (epoch == Epoch.IRON) {
			// Po železe se počet dní (ani podobný odpočet) neukazuje.
			return Component.literal(epoch.czechName() + ".");
		}
		return Component.literal(epoch.czechName() + ". Odehráno " + minutesPlayed(data.playTicks()) + " min.");
	}

	public void tellJoiningPlayer(ServerPlayer player) {
		BychRuntime runtime = BychRuntime.get();
		PlaytimeStore store = PlaytimeStore.get(player.getServer());
		PlayerEpochData data = store.of(player.getUUID());
		Epoch epoch = runtime.clock().epochForPlayTicks(data.playTicks(), runtime.config());
		data.setLastAnnouncedEpoch(epoch);
		EpochSync.sync(player, epoch);
		player.sendSystemMessage(this.describe(player));
	}

	/**
	 * Každý krok: přičte online čas a při změně doby ukáže nápis + zvuk.
	 * Volá se z hlavního světa, ať se stejná práce nedělá třikrát.
	 */
	public void onOverworldTick(MinecraftServer server) {
		BychRuntime runtime = BychRuntime.get();
		PlaytimeStore store = PlaytimeStore.get(server);
		BychConfig config = runtime.config();
		EpochClock clock = runtime.clock();
		boolean dirty = false;

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			PlayerEpochData data = store.of(player.getUUID());
			data.addPlayTick();
			dirty = true;

			Epoch epoch = clock.epochForPlayTicks(data.playTicks(), config);
			Epoch last = data.lastAnnouncedEpoch();
			if (last == null) {
				data.setLastAnnouncedEpoch(epoch);
				EpochSync.sync(player, epoch);
				continue;
			}
			if (epoch == last) {
				continue;
			}
			data.setLastAnnouncedEpoch(epoch);
			EpochSync.sync(player, epoch);
			this.announceTransition(player, epoch);
		}

		if (dirty) {
			store.markChanged();
		}
	}

	/** Po setage / reset řekne novou dobu i tehdy, když se playtime skočil ručně. */
	public void tellAfterAgeChange(ServerPlayer player) {
		BychRuntime runtime = BychRuntime.get();
		PlaytimeStore store = PlaytimeStore.get(player.getServer());
		PlayerEpochData data = store.of(player.getUUID());
		Epoch epoch = runtime.clock().epochForPlayTicks(data.playTicks(), runtime.config());
		data.setLastAnnouncedEpoch(epoch);
		EpochSync.sync(player, epoch);
		if (data.medieval()) {
			this.showTitle(player, "Středověk");
		} else {
			this.showTitle(player, epoch.czechName());
		}
		player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0F, 1.0F);
		player.sendSystemMessage(this.describe(player));
	}

	private void announceTransition(ServerPlayer player, Epoch epoch) {
		this.showTitle(player, epoch.czechName());
		player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0F, 1.0F);
		player.sendSystemMessage(this.describe(player));
	}

	private void showTitle(ServerPlayer player, String title) {
		player.connection.send(new ClientboundClearTitlesPacket(false));
		player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20));
		player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(title)));
		player.connection.send(new ClientboundSetSubtitleTextPacket(Component.empty()));
	}

	private static long minutesPlayed(long playTicks) {
		return playTicks / EpochClock.TICKS_PER_MINUTE;
	}
}
