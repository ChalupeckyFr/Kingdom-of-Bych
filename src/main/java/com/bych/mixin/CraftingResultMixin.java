package com.bych.mixin;

import com.bych.epoch.StoneAgeRestrictions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Slot výsledku je ten, odkud hráč bere vyrobenou věc.
 * Platí pro stůl, malou mřížku v inventáři i kovadlinu na netherit.
 * Když je věc zakázaná, nejde ji z toho slotu vzít.
 */
@Mixin(Slot.class)
public class CraftingResultMixin {
	@Shadow
	public net.minecraft.world.Container container;

	@Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
	private void kingdomOfBych$blockStoneAgeResult(Player player, CallbackInfoReturnable<Boolean> callback) {
		if (!(this.container instanceof ResultContainer)) {
			return;
		}
		ItemStack stack = ((Slot) (Object) this).getItem();
		if (StoneAgeRestrictions.denyCraftResult(player, stack)) {
			callback.setReturnValue(false);
		}
	}
}
