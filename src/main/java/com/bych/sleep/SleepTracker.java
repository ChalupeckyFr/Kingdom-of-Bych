package com.bych.sleep;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.bych.KingdomOfBych;

import net.minecraft.server.level.ServerPlayer;

/**
 * Pamatuje si, od kterého ticku každý hráč leží v posteli.
 * Tick je jeden krok hry. 20 ticků je jedna sekunda.
 * Čas bereme z času světa, ne z hodin na zdi, aby pauza hry čekání taky zastavila.
 */
public final class SleepTracker {
	private final Map<UUID, Long> startedAtGameTime = new HashMap<>();
	/** Aby se do logu nepsala pořád stejná věta, jakmile hráč čas odleží. */
	private final Set<UUID> loggedReady = new HashSet<>();

	public void start(ServerPlayer player, int requiredSeconds) {
		this.startedAtGameTime.put(player.getUUID(), player.level().getGameTime());
		this.loggedReady.remove(player.getUUID());
		KingdomOfBych.LOGGER.info(
				"Hráč {} si lehl. Noc počká nejméně {} sekund.",
				player.getName().getString(),
				requiredSeconds
		);
	}

	public void forget(ServerPlayer player) {
		this.startedAtGameTime.remove(player.getUUID());
		this.loggedReady.remove(player.getUUID());
	}

	public void clear() {
		this.startedAtGameTime.clear();
		this.loggedReady.clear();
	}

	public boolean hasSleptLongEnough(ServerPlayer player, int requiredSeconds) {
		if (requiredSeconds <= 0) {
			return true;
		}
		Long startedAt = this.startedAtGameTime.get(player.getUUID());
		if (startedAt == null) {
			return false;
		}
		long elapsedTicks = player.level().getGameTime() - startedAt;
		boolean ready = elapsedTicks >= requiredSeconds * 20L;
		if (ready && this.loggedReady.add(player.getUUID())) {
			KingdomOfBych.LOGGER.info(
					"Hráč {} už leží dost dlouho. Noc se může přeskočit, pokud spí dost lidí.",
					player.getName().getString()
			);
		}
		return ready;
	}
}
