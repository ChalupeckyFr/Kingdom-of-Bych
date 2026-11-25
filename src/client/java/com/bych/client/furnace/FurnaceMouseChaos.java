package com.bych.client.furnace;

import com.bych.BychRuntime;
import com.bych.config.BychConfig;
import com.bych.epoch.StoneAgeFurnace;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;

/**
 * Minihra u pece v době kamenné.
 * Mění citlivost myši a posouvá místo kliku, aby se pec nedala normálně ovládat.
 * Ohniště tuhle třídu neřeší — platí jen pro okno pece, tavicí pece a udírny.
 */
public final class FurnaceMouseChaos {
	private static BychConfig clientConfig = BychConfig.loadOrCreate();
	private static boolean active;
	private static Double savedSensitivity;
	private static boolean toldPlayer;

	private FurnaceMouseChaos() {
	}

	public static void reloadClientConfig() {
		clientConfig = BychConfig.loadOrCreate();
	}

	/**
	 * Každý klientský tick: zapne nebo vypne chaos podle otevřeného okna a doby.
	 */
	public static void tick(Minecraft minecraft) {
		if (shouldBeActive(minecraft)) {
			if (!active) {
				activate(minecraft);
			}
			pulseSensitivity(minecraft);
		} else if (active) {
			deactivate(minecraft);
		}
	}

	public static boolean isActive() {
		return active;
	}

	/**
	 * Násobek pohybu myši od středu okna.
	 * Mění se v čase — v okně pece je myš střídavě těžkopádná a divoká.
	 */
	public static double movementGain() {
		double t = System.currentTimeMillis() / 1000.0;
		double wave = (Math.sin(t * 2.6) + 1.0) * 0.5;
		return 0.2 + wave * 2.3;
	}

	/**
	 * Posun kliku v souřadnicích okna pece.
	 * Kurzor může ukazovat na slot, ale klik dopadne vedle.
	 */
	public static double offsetXGui(Minecraft minecraft) {
		double t = System.currentTimeMillis() / 1000.0;
		double pixels = Math.sin(t * 3.1) * 56.0 + Math.sin(t * 7.2) * 18.0;
		return pixels * minecraft.getWindow().getGuiScaledWidth() / (double) minecraft.getWindow().getScreenWidth();
	}

	public static double offsetYGui(Minecraft minecraft) {
		double t = System.currentTimeMillis() / 1000.0;
		double pixels = Math.cos(t * 2.7) * 56.0 + Math.cos(t * 6.3) * 18.0;
		return pixels * minecraft.getWindow().getGuiScaledHeight() / (double) minecraft.getWindow().getScreenHeight();
	}

	private static boolean shouldBeActive(Minecraft minecraft) {
		if (!(minecraft.screen instanceof AbstractFurnaceScreen<?>)) {
			return false;
		}
		if (minecraft.level == null) {
			return false;
		}
		return StoneAgeFurnace.shouldChaosMouse(minecraft.level.getDayTime(), config());
	}

	private static void activate(Minecraft minecraft) {
		active = true;
		savedSensitivity = minecraft.options.sensitivity().get();
		if (!toldPlayer && minecraft.player != null) {
			minecraft.player.displayClientMessage(
					Component.literal("V době kamenné pec ještě zlobí. Myš blázní a klik míří jinam."),
					true
			);
			toldPlayer = true;
		}
	}

	private static void deactivate(Minecraft minecraft) {
		active = false;
		if (savedSensitivity != null) {
			minecraft.options.sensitivity().set(savedSensitivity);
			savedSensitivity = null;
		}
		toldPlayer = false;
	}

	/** Nastavení citlivosti nahoře a dole, ať je změna vidět i v ovládání. */
	private static void pulseSensitivity(Minecraft minecraft) {
		double t = System.currentTimeMillis() / 1000.0;
		double wave = (Math.sin(t * 3.0) + 1.0) * 0.5;
		minecraft.options.sensitivity().set(wave);
	}

	private static BychConfig config() {
		BychRuntime runtime = BychRuntime.get();
		if (runtime != null) {
			return runtime.config();
		}
		return clientConfig;
	}
}
