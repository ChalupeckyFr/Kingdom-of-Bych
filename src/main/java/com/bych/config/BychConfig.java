package com.bych.config;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

/**
 * Ovládací panel modu.
 * Je to obyčejný textový soubor, aby šel otevřít v Poznámkovém bloku.
 * Řádky začínající # jsou české komentáře. Hra je při čtení přeskakuje.
 * Když soubor chybí, založí se s výchozími hodnotami, které František potvrdil.
 */
public final class BychConfig {
	private static final String FILE_NAME = "kingdom-of-bych.properties";
	/** Poznámkový blok ve Windows pozná češtinu spolehlivě, když je na začátku souboru značka UTF-8. */
	private static final String UTF8_BOM = "\uFEFF";

	private static final String TEMPLATE = """
			# Ovládací panel Kingdom of Bych.
			# Řádky s # jsou komentáře. Čísla pod nimi můžeš změnit.
			# Ve hře pak napiš /kbych reload a hra se nemusí zavírat.

			# Kolik procent hráčů musí ležet v posteli, aby se noc mohla přeskočit.
			# 100 znamená všichni. S jedním hráčem je v posteli vždycky celých 100 procent,
			# takže 50 a 100 u jednoho hráče dopadne stejně. Rozdíl uvidíš až ve více lidech.
			sleep.required-player-percent=50

			# Kolik sekund musí spánek trvat, než se noc smí přeskočit.
			# Noc skončí, až platí procenta i tenhle čas zároveň.
			# Má to zabránit tomu, že si hráč jen lehne a hned přeskočí celou noc.
			sleep.required-seconds=20

			# Po kolika minutách odehraného času (online ve světě, včetně AFK) přijde doba bronzová.
			epoch.bronze-after-minutes=10

			# Kolik dalších minut po začátku bronzu uplyne do doby železné.
			epoch.iron-after-minutes=20

			# Nejvyšší počet vojáků u jednoho vlajkonoše.
			# Oddíly ještě nejsou. Číslo se jen uloží, aby o něj panel nepřišel.
			army.max-soldiers=9
			""";

	private final int requiredPlayerPercent;
	private final int requiredSleepSeconds;
	private final int bronzeAfterMinutes;
	private final int ironAfterMinutes;
	private final int maxSoldiers;
	private final List<String> warnings;

	private BychConfig(
			int requiredPlayerPercent,
			int requiredSleepSeconds,
			int bronzeAfterMinutes,
			int ironAfterMinutes,
			int maxSoldiers,
			List<String> warnings
	) {
		this.requiredPlayerPercent = requiredPlayerPercent;
		this.requiredSleepSeconds = requiredSleepSeconds;
		this.bronzeAfterMinutes = bronzeAfterMinutes;
		this.ironAfterMinutes = ironAfterMinutes;
		this.maxSoldiers = maxSoldiers;
		this.warnings = List.copyOf(warnings);
	}

	public static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
	}

	/**
	 * Načte soubor. Když ještě není na disku, zapíše šablonu s českými komentáři.
	 * Rozbitou hodnotu neháže hru. Vrátí potvrzený výchozí údaj a přidá větu do upozornění.
	 */
	public static BychConfig loadOrCreate() {
		Path path = path();
		try {
			Files.createDirectories(path.getParent());
			if (Files.notExists(path)) {
				Files.writeString(path, UTF8_BOM + TEMPLATE.replace("\n", "\r\n"), StandardCharsets.UTF_8);
			}
			String text = Files.readString(path, StandardCharsets.UTF_8);
			if (text.startsWith(UTF8_BOM)) {
				text = text.substring(1);
			}
			Properties properties = new Properties();
			properties.load(new StringReader(text));
			return fromProperties(properties);
		} catch (IOException exception) {
			return confirmedDefaults(List.of(
					"Soubor s nastavením nešel přečíst. Použil jsem výchozí hodnoty."
			));
		}
	}

	public int requiredPlayerPercent() {
		return this.requiredPlayerPercent;
	}

	public int requiredSleepSeconds() {
		return this.requiredSleepSeconds;
	}

	public int bronzeAfterMinutes() {
		return this.bronzeAfterMinutes;
	}

	public int ironAfterMinutes() {
		return this.ironAfterMinutes;
	}

	public int maxSoldiers() {
		return this.maxSoldiers;
	}

	public List<String> warnings() {
		return this.warnings;
	}

	/** Jedna česká věta do chatu po příkazu /kbych reload a do logu při startu. */
	public Component summary() {
		String text = "Nastavení načteno. Spánek: " + this.requiredPlayerPercent
				+ " % hráčů a " + this.requiredSleepSeconds
				+ " sekund. Bronz po " + this.bronzeAfterMinutes
				+ " min odehraného času, železo po dalších " + this.ironAfterMinutes
				+ " min. Vojáků v oddílu nejvýš " + this.maxSoldiers + ".";
		if (!this.warnings.isEmpty()) {
			text = text + " " + String.join(" ", this.warnings);
		}
		return Component.literal(text);
	}

	private static BychConfig fromProperties(Properties properties) {
		List<String> warnings = new ArrayList<>();
		int percent = readInt(properties, "sleep.required-player-percent", 50, 0, 100, warnings);
		int seconds = readInt(properties, "sleep.required-seconds", 20, 0, 24 * 60 * 60, warnings);
		int bronzeMinutes = readInt(properties, "epoch.bronze-after-minutes", 10, 0, 24 * 60, warnings);
		int ironMinutes = readInt(properties, "epoch.iron-after-minutes", 20, 0, 24 * 60, warnings);
		int soldiers = readInt(properties, "army.max-soldiers", 9, 0, 9, warnings);
		return new BychConfig(percent, seconds, bronzeMinutes, ironMinutes, soldiers, warnings);
	}

	private static BychConfig confirmedDefaults(List<String> warnings) {
		return new BychConfig(50, 20, 10, 20, 9, warnings);
	}

	/**
	 * Číslo mimo povolený rozsah nebereme. Místo něj zůstane výchozí hodnota
	 * a do upozornění se přidá věta, ať je v chatu vidět, co se stalo.
	 */
	private static int readInt(
			Properties properties,
			String key,
			int fallback,
			int minimum,
			int maximum,
			List<String> warnings
	) {
		String raw = properties.getProperty(key);
		if (raw == null || raw.isBlank()) {
			warnings.add("Chybí " + key + ", beru " + fallback + ".");
			return fallback;
		}
		try {
			int value = Integer.parseInt(raw.trim());
			if (value < minimum || value > maximum) {
				warnings.add(key + " je mimo rozsah, beru " + fallback + ".");
				return fallback;
			}
			return value;
		} catch (NumberFormatException exception) {
			warnings.add(key + " není celé číslo, beru " + fallback + ".");
			return fallback;
		}
	}
}
