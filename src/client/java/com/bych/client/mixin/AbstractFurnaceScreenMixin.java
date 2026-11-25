package com.bych.client.mixin;

import com.bych.client.furnace.FurnaceMouseChaos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Klik v okně pece dopadne jinam než ukazatel myši.
 * Jistota navíc k posunu v MouseHandler — tady saháme přímo na souřadnice kliku.
 */
@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin {
	@ModifyVariable(method = "mouseClicked", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private double kingdomOfBych$offsetClickX(double mouseX) {
		if (!FurnaceMouseChaos.isActive()) {
			return mouseX;
		}
		return mouseX + FurnaceMouseChaos.offsetXGui(Minecraft.getInstance());
	}

	@ModifyVariable(method = "mouseClicked", at = @At("HEAD"), ordinal = 1, argsOnly = true)
	private double kingdomOfBych$offsetClickY(double mouseY) {
		if (!FurnaceMouseChaos.isActive()) {
			return mouseY;
		}
		return mouseY + FurnaceMouseChaos.offsetYGui(Minecraft.getInstance());
	}
}
