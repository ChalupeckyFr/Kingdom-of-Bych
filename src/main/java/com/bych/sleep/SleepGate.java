package com.bych.sleep;

import com.bych.BychRuntime;
import com.bych.config.BychConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;

/**
 * Drží noční přeskočení, dokud neplatí obojí: dost hráčů spí a odleželi nastavené sekundy.
 * Procenta nastavujeme pravidlem, které Minecraft už umí.
 * Sekundy Minecraft sám nemá, proto je hlídáme tady.
 */
public final class SleepGate {
	private SleepGate() {
	}

	public static void applyPercentRule(ServerLevel level, int percent) {
		// Soubor je zdroj pravdy. Pravidlo světa se při startu a při /kbych reload přepíše podle něj.
		level.getGameRules()
				.getRule(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE)
				.set(percent, level.getServer());
	}

	/**
	 * True znamená: lidé už leží, ale ještě neuběhl čas, takže noc teď přeskočit nesmí.
	 * Když nikdo neleží, vrací false a Minecraft si noc řeší po svém.
	 */
	public static boolean shouldHoldNightSkip(ServerLevel level) {
		BychConfig config = BychRuntime.get().config();
		if (config.requiredSleepSeconds() <= 0) {
			return false;
		}
		int sleeping = 0;
		int ready = 0;
		for (ServerPlayer player : level.players()) {
			if (!player.isSleeping()) {
				continue;
			}
			sleeping++;
			if (BychRuntime.get().sleepTracker().hasSleptLongEnough(player, config.requiredSleepSeconds())) {
				ready++;
			}
		}
		if (sleeping == 0) {
			return false;
		}
		int needed = sleepersNeeded(activePlayers(level), config.requiredPlayerPercent());
		return ready < needed;
	}

	public static boolean anyoneSleeping(ServerLevel level) {
		for (ServerPlayer player : level.players()) {
			if (player.isSleeping()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Stejné zaokrouhlení, jaké používá Minecraft: půl hráče se zaokrouhlí nahoru
	 * a aspoň jeden spáč je potřeba, když je procento větší než nula.
	 * U jednoho hráče je 50 procent pořád jeden člověk, takže noc přeskočit jde.
	 */
	static int sleepersNeeded(int activePlayers, int percent) {
		if (activePlayers <= 0 || percent <= 0) {
			return 0;
		}
		return Math.max(1, Mth.ceil(activePlayers * (percent / 100.0F)));
	}

	private static int activePlayers(ServerLevel level) {
		int count = 0;
		for (ServerPlayer player : level.players()) {
			if (!player.isSpectator()) {
				count++;
			}
		}
		return count;
	}
}
