package com.bych.epoch;

import com.bych.config.BychConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Počítá dobu z odehraného času hráče a den světa zvlášť (pro noc / lobby).
 * Výpočet doby je schválně na jednom místě.
 * Ve fázi 1 playtime běží, dokud je hráč online. Až bude autobus, stejné místo
 * se přepne na start od odletu — do té doby se sem autobus nenapojuje.
 */
public final class EpochClock {
	/** Jeden minecraftí den má 24000 ticků. Tick je krok hry, 20 ticků je jedna sekunda. */
	public static final long TICKS_PER_DAY = 24000L;
	public static final int TICKS_PER_MINUTE = 20 * 60;

	public int currentDay(ServerLevel overworld) {
		return this.dayFromDayTime(overworld.getDayTime());
	}

	/**
	 * Stejný výpočet dne jako u světa, ale z holého denního času.
	 * Den světa už neurčuje dobu — slouží spánku, noci a lobby.
	 */
	public int dayFromDayTime(long dayTime) {
		long clockStartDayTime = 0L;
		long elapsed = dayTime - clockStartDayTime;
		if (elapsed < 0L) {
			elapsed = 0L;
		}
		return (int) (elapsed / TICKS_PER_DAY) + 1;
	}

	public void applyDay(ServerLevel overworld, int day) {
		overworld.setDayTime(this.dayTimeForDay(day));
	}

	/** Prah bronzu v ticích odehraného času. */
	public long bronzeThresholdTicks(BychConfig config) {
		return (long) config.bronzeAfterMinutes() * TICKS_PER_MINUTE;
	}

	/** Prah železa = bronz + další minuty. */
	public long ironThresholdTicks(BychConfig config) {
		return this.bronzeThresholdTicks(config) + (long) config.ironAfterMinutes() * TICKS_PER_MINUTE;
	}

	public Epoch epochForPlayTicks(long playTicks, BychConfig config) {
		if (playTicks >= this.ironThresholdTicks(config)) {
			return Epoch.IRON;
		}
		if (playTicks >= this.bronzeThresholdTicks(config)) {
			return Epoch.BRONZE;
		}
		return Epoch.STONE;
	}

	public Epoch epochOf(ServerPlayer player, PlaytimeStore store, BychConfig config) {
		return this.epochForPlayTicks(store.of(player.getUUID()).playTicks(), config);
	}

	/**
	 * Nastaví odehraný čas (a středověk) tak, aby hráč byl přesně v dané době.
	 * U středověku se playtime aspoň dorovná na železo; vesnice ho zatím neodemyká.
	 */
	public void applyAge(ServerPlayer player, AgeTarget age, PlaytimeStore store, BychConfig config) {
		PlayerEpochData data = store.of(player.getUUID());
		switch (age) {
			case STONE -> {
				data.setPlayTicks(0L);
				data.setMedieval(false);
			}
			case BRONZE -> {
				data.setPlayTicks(this.bronzeThresholdTicks(config));
				data.setMedieval(false);
			}
			case IRON -> {
				data.setPlayTicks(this.ironThresholdTicks(config));
				data.setMedieval(false);
			}
			case MEDIEVAL -> {
				data.setPlayTicks(Math.max(data.playTicks(), this.ironThresholdTicks(config)));
				data.setMedieval(true);
			}
		}
		store.markChanged();
	}

	private long dayTimeForDay(int day) {
		long clockStartDayTime = 0L;
		int safeDay = Math.max(1, day);
		return clockStartDayTime + (safeDay - 1L) * TICKS_PER_DAY;
	}
}
