package com.bych.epoch;

/**
 * Cíl příkazu setage. Středověk sem patří jen jako zkušební přepínač —
 * playtime ho sám neodemyká.
 */
public enum AgeTarget {
	STONE("stone", "Doba kamenná"),
	BRONZE("bronze", "Doba bronzová"),
	IRON("iron", "Doba železná"),
	MEDIEVAL("medieval", "Středověk");

	private final String commandName;
	private final String czechName;

	AgeTarget(String commandName, String czechName) {
		this.commandName = commandName;
		this.czechName = czechName;
	}

	public String commandName() {
		return this.commandName;
	}

	public String czechName() {
		return this.czechName;
	}

	public static AgeTarget fromCommandName(String raw) {
		for (AgeTarget age : values()) {
			if (age.commandName.equalsIgnoreCase(raw)) {
				return age;
			}
		}
		return null;
	}
}
