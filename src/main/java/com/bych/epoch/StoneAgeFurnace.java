package com.bych.epoch;

/**
 * Pravidlo pece v době kamenné.
 * Pec se smí otevřít, ale v okně musí blbnout myš.
 * Ohniště sem nepatří — tohle platí jen pro pec, tavicí pec a udírnu.
 * Doba se bere z hráče (playtime), ne z dne světa.
 */
public final class StoneAgeFurnace {
	private StoneAgeFurnace() {
	}

	/** Ano, když je hráč v době kamenné. */
	public static boolean shouldChaosMouse(Epoch epoch) {
		return epoch == Epoch.STONE;
	}
}
