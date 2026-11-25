package com.bych.client.mixin;

import com.bych.client.furnace.FurnaceMouseChaos;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * V okně pece přepočítá pohyb myši podle měnící se citlivosti.
 * Skutečný kurzor systému zůstane, ale hra čte jinou pozici.
 */
@Mixin(MouseHandler.class)
public class FurnaceMouseHandlerMixin {
	@Shadow
	private double xpos;

	@Shadow
	private double ypos;

	@Inject(method = "onMove", at = @At("TAIL"))
	private void kingdomOfBych$warpFurnaceMouse(long window, double x, double y, CallbackInfo ci) {
		if (!FurnaceMouseChaos.isActive()) {
			return;
		}
		Minecraft minecraft = Minecraft.getInstance();
		double midX = minecraft.getWindow().getScreenWidth() / 2.0;
		double midY = minecraft.getWindow().getScreenHeight() / 2.0;
		double gain = FurnaceMouseChaos.movementGain();
		// x a y jsou skutečná pozice. Gain dělá z pohybu citlivější nebo tupější.
		this.xpos = midX + (x - midX) * gain;
		this.ypos = midY + (y - midY) * gain;
	}
}
