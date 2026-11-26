package com.bych.mixin;

import com.bych.epoch.StoneAgeRestrictions;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Zakázaný blok, třeba pec, nejde položit.
 * Klik na postel, dveře nebo stůl se tím nesmí spolknout.
 * PASS znamená: nepokládej to a nech klik doběhnout k tomu bloku.
 */
@Mixin(BlockItem.class)
public class ForbiddenPlacementMixin {
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void kingdomOfBych$blockStoneAgePlacement(UseOnContext context, CallbackInfoReturnable<InteractionResult> callback) {
		Player player = context.getPlayer();
		if (player == null) {
			return;
		}
		if (!StoneAgeRestrictions.isBlocked(player, context.getLevel(), context.getItemInHand())) {
			return;
		}
		BlockState clicked = context.getLevel().getBlockState(context.getClickedPos());
		if (!keepsTheClick(player, context.getLevel(), clicked)) {
			StoneAgeRestrictions.tellBlocked(player);
		}
		callback.setReturnValue(InteractionResult.PASS);
	}

	private static boolean keepsTheClick(Player player, Level level, BlockState state) {
		ItemStack asItem = state.getBlock().asItem().getDefaultInstance();
		if (StoneAgeRestrictions.isBlocked(player, level, asItem)) {
			return false;
		}
		var block = state.getBlock();
		return block instanceof BedBlock
				|| block instanceof DoorBlock
				|| block instanceof TrapDoorBlock
				|| block instanceof FenceGateBlock
				|| block instanceof CraftingTableBlock
				|| block instanceof CampfireBlock
				|| state.hasBlockEntity();
	}
}
