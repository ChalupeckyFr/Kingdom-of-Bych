package com.bych.epoch;

/**
 * Trvalý stav jednoho hráče: odehrané tickky a jestli už má středověk (zatím jen přes setage).
 */
public final class PlayerEpochData {
	private long playTicks;
	private boolean medieval;
	/** Naposledy ohlášená doba. Nepersistuje se — po restartu se jen znovu zapamatuje. */
	private Epoch lastAnnouncedEpoch;

	public long playTicks() {
		return this.playTicks;
	}

	public void setPlayTicks(long playTicks) {
		this.playTicks = Math.max(0L, playTicks);
	}

	public void addPlayTick() {
		this.playTicks += 1L;
	}

	public boolean medieval() {
		return this.medieval;
	}

	public void setMedieval(boolean medieval) {
		this.medieval = medieval;
	}

	public Epoch lastAnnouncedEpoch() {
		return this.lastAnnouncedEpoch;
	}

	public void setLastAnnouncedEpoch(Epoch epoch) {
		this.lastAnnouncedEpoch = epoch;
	}
}
