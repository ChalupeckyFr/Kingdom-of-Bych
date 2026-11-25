package com.bych.epoch;

import com.bych.config.BychConfig;

/**
 * Pravidlo pece v době kamenné.
 * Pec se smí otevřít, ale v okně musí blbnout myš.
 * Ohniště sem nepatří — tohle platí jen pro pec, tavicí pec a udírnu.
 */
public final class StoneAgeFurnace {
	private static final EpochClock CLOCK = new EpochClock();

	private StoneAgeFurnace() {
	}

	/**
	 * Ano, když je svět v době kamenné.
	 * Denní čas bere klient ze světa, který už server poslal.
	 * Nastavení pece nepočítá samo — bere stejný ovládací soubor jako zbytek epoch.
	 */
	public static boolean shouldChaosMouse(long dayTime, BychConfig config) {
		return CLOCK.epochForDayTime(dayTime, config) == Epoch.STONE;
	}
}
