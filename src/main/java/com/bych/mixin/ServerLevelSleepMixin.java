package com.bych.mixin;

import java.util.List;

import com.bych.sleep.SleepGate;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Minecraft v každém kroku světa zkontroluje, jestli už spí dost lidí,
 * a když ano, hned posune čas na ráno.
 * My tu kontrolu na chvíli shodíme, dokud neuběhnou sekundy z ovládacího souboru.
 * Zbytek kroku světa běží dál. Nezastavujeme počasí ani ostatní věci.
 */
@Mixin(ServerLevel.class)
public class ServerLevelSleepMixin {
	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/players/SleepStatus;areEnoughDeepSleeping(ILjava/util/List;)Z"
			)
	)
	private boolean kingdomOfBych$waitForSleepSeconds(SleepStatus status, int percentage, List<ServerPlayer> players) {
		if (SleepGate.shouldHoldNightSkip((ServerLevel) (Object) this)) {
			return false;
		}
		return status.areEnoughDeepSleeping(percentage, players);
	}
}
