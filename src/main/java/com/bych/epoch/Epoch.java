package com.bych.epoch;

/**
 * Světová doba. Středověk sem nepatří.
 * Ten se později odemyká každému hráči zvlášť, až bude umět obsadit nebo založit vesnici.
 */
public enum Epoch {
	STONE("Doba kamenná"),
	BRONZE("Doba bronzová"),
	IRON("Doba železná");

	private final String czechName;

	Epoch(String czechName) {
		this.czechName = czechName;
	}

	public String czechName() {
		return this.czechName;
	}
}
