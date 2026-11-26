package com.bych.epoch;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Pošle klientovi číslo doby přes scoreboard, ať pec ví, jestli má blbnout myš.
 * 0 kámen, 1 bronz, 2 železo. Středověk pec neřeší — chaos je jen v kameni.
 */
public final class EpochSync {
	public static final String OBJECTIVE_NAME = "kbych_epoch";

	private EpochSync() {
	}

	public static void ensureObjective(MinecraftServer server) {
		Scoreboard scoreboard = server.getScoreboard();
		if (scoreboard.getObjective(OBJECTIVE_NAME) != null) {
			return;
		}
		scoreboard.addObjective(
				OBJECTIVE_NAME,
				ObjectiveCriteria.DUMMY,
				net.minecraft.network.chat.Component.literal("kbych_epoch"),
				ObjectiveCriteria.RenderType.INTEGER,
				false,
				null
		);
	}

	public static void sync(ServerPlayer player, Epoch epoch) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		ensureObjective(server);
		Scoreboard scoreboard = server.getScoreboard();
		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null) {
			return;
		}
		ScoreAccess access = scoreboard.getOrCreatePlayerScore(player, objective);
		access.set(epoch.ordinal());
	}

	/**
	 * Klient: přečte dobu z scoreboardu. Když chybí, bere kámen (opatrnější pro pec).
	 */
	public static Epoch readClientEpoch(Scoreboard scoreboard, net.minecraft.world.entity.player.Player player) {
		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null) {
			return Epoch.STONE;
		}
		var info = scoreboard.getPlayerScoreInfo(player, objective);
		if (info == null) {
			return Epoch.STONE;
		}
		int value = info.value();
		Epoch[] values = Epoch.values();
		if (value < 0 || value >= values.length) {
			return Epoch.STONE;
		}
		return values[value];
	}
}
