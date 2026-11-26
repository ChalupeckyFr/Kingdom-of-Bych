package com.bych;

import com.bych.config.BychConfig;
import com.bych.epoch.EpochAnnouncer;
import com.bych.epoch.EpochClock;
import com.bych.epoch.StoneAgeRestrictions;
import com.bych.sleep.SleepGate;
import com.bych.sleep.SleepTracker;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Jedno společné místo, odkud příkazy i události berou nastavení, hodiny a spánek.
 * Žije po celou dobu běhu hry. Při /kbych reload se vymění jen načtený soubor.
 */
public final class BychRuntime {
	private static BychRuntime instance;

	private BychConfig config;
	private final EpochClock clock = new EpochClock();
	private final EpochAnnouncer announcer = new EpochAnnouncer();
	private final SleepTracker sleepTracker = new SleepTracker();

	private BychRuntime(BychConfig config) {
		this.config = config;
	}

	public static void start(BychConfig config) {
		instance = new BychRuntime(config);
	}

	public static BychRuntime get() {
		return instance;
	}

	public BychConfig config() {
		return this.config;
	}

	public EpochClock clock() {
		return this.clock;
	}

	public EpochAnnouncer announcer() {
		return this.announcer;
	}

	public SleepTracker sleepTracker() {
		return this.sleepTracker;
	}

	public void reloadConfig() {
		this.config = BychConfig.loadOrCreate();
	}

	public void applySleepRule(MinecraftServer server) {
		int percent = this.config.requiredPlayerPercent();
		for (ServerLevel level : server.getAllLevels()) {
			SleepGate.applyPercentRule(level, percent);
		}
		KingdomOfBych.LOGGER.info("Pravidlo spánku nastaveno na {} procent hráčů.", percent);
	}

	public void onWorldTick(ServerLevel level) {
		if (level.dimension() == Level.OVERWORLD) {
			this.announcer.onOverworldTick(level.getServer());
			for (var player : level.players()) {
				StoneAgeRestrictions.stripForbiddenArmor(player);
			}
		}
	}

	public void clearSleepers() {
		this.sleepTracker.clear();
	}
}
