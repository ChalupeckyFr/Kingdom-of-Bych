package com.bych.epoch;

import com.bych.config.BychConfig;

import net.minecraft.server.level.ServerLevel;

/**
 * Počítá, který je herní den a která doba z něj plyne.
 * Výpočet je schválně na jednom místě.
 * Teď den 1 začíná založením světa, tedy od denního času 0.
 * Až bude autobus, odlet nastaví začátek na čas odletu. Do té doby se sem autobus nenapojuje.
 * Denní čas světa si ukládá sám Minecraft, proto ho znovu neukládáme.
 */
public final class EpochClock {
	/** Jeden minecraftí den má 24000 ticků. Tick je krok hry, 20 ticků je jedna sekunda. */
	public static final long TICKS_PER_DAY = 24000L;

	public int currentDay(ServerLevel overworld) {
		return this.dayFromDayTime(overworld.getDayTime());
	}

	/**
	 * Stejný výpočet dne jako u světa, ale z holého denního času.
	 * Klient ho potřebuje u pece, protože nemá ServerLevel.
	 */
	public int dayFromDayTime(long dayTime) {
		// Začátek hodin. Ve fázi 1 je to nula, tedy založení světa.
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

	public Epoch epochForDay(int day, BychConfig config) {
		// Železo je od svého dne trvalé, proto vyhrává i kdyby se rozsahy v souboru překryly.
		if (day >= config.ironAgeFirstDay()) {
			return Epoch.IRON;
		}
		if (day >= config.bronzeAgeFirstDay() && day <= config.bronzeAgeLastDay()) {
			return Epoch.BRONZE;
		}
		return Epoch.STONE;
	}

	/** Klient i server: doba přímo z denního času světa. */
	public Epoch epochForDayTime(long dayTime, BychConfig config) {
		return this.epochForDay(this.dayFromDayTime(dayTime), config);
	}

	private long dayTimeForDay(int day) {
		long clockStartDayTime = 0L;
		int safeDay = Math.max(1, day);
		return clockStartDayTime + (safeDay - 1L) * TICKS_PER_DAY;
	}
}
