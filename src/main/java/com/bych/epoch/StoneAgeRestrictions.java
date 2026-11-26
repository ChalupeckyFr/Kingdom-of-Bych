package com.bych.epoch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Zakázané věci se nesmí vyrobit ani použít podle doby.
 * Rozhoduje server. Klient jen dostane českou větu do chatu.
 */
public final class StoneAgeRestrictions {
	private static final Component DENIED = Component.literal("V téhle době tohle nejde vyrobit ani použít.");
	private static final Map<UUID, Long> nextMessageAt = new HashMap<>();

	private StoneAgeRestrictions() {
	}

	public static void register() {
		UseItemCallback.EVENT.register((player, level, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (deny(player, level, stack)) {
				return InteractionResultHolder.fail(stack);
			}
			return InteractionResultHolder.pass(stack);
		});
		// Kontroluje se jen blok, na který hráč klikl. Věc v ruce tu být nesmí,
		// jinak by pec v ruce zablokovala i postel.
		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			ItemStack clicked = level.getBlockState(hit.getBlockPos()).getBlock().asItem().getDefaultInstance();
			if (deny(player, level, clicked)) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
			if (deny(player, level, player.getMainHandItem())) {
				return false;
			}
			return true;
		});
		AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
			if (deny(player, level, player.getItemInHand(hand))) {
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
	}

	/** Výroba se kontroluje ve slotu výsledku. Suroviny v mřížce se berou z otevřeného stolu. */
	public static boolean denyCraftResult(Player player, ItemStack stack) {
		if (stack.isEmpty() || !(player instanceof ServerPlayer serverPlayer)) {
			return false;
		}
		List<ItemStack> ingredients = ingredientsInOpenMenu(serverPlayer);
		String reason = CraftPolicy.blockReason(serverPlayer, stack, ingredients);
		if (reason.isEmpty()) {
			return false;
		}
		tell(serverPlayer);
		return true;
	}

	/**
	 * Nasazené zakázané brnění sundá, protože nošení je používání.
	 * Volá se jen z hlavního světa, ať se stejná kontrola nedělá třikrát za jeden krok.
	 */
	public static void stripForbiddenArmor(ServerPlayer player) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}
			ItemStack stack = player.getItemBySlot(slot);
			if (CraftPolicy.blockReason(player, stack, List.of()).isEmpty()) {
				continue;
			}
			player.setItemSlot(slot, ItemStack.EMPTY);
			if (!player.getInventory().add(stack)) {
				player.drop(stack, false);
			}
			tell(player);
		}
	}

	/** Jen kontrola, bez věty do chatu. Pokládání bloku si zprávu řeší samo. */
	public static boolean isBlocked(Player player, Level level, ItemStack stack) {
		if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer) || stack.isEmpty()) {
			return false;
		}
		return !CraftPolicy.blockReason(serverPlayer, stack, List.of()).isEmpty();
	}

	public static void tellBlocked(Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			tell(serverPlayer);
		}
	}

	private static List<ItemStack> ingredientsInOpenMenu(ServerPlayer player) {
		List<ItemStack> ingredients = new ArrayList<>();
		for (Slot slot : player.containerMenu.slots) {
			if (slot.container == player.getInventory() || slot.container instanceof ResultContainer) {
				continue;
			}
			if (!slot.getItem().isEmpty()) {
				ingredients.add(slot.getItem());
			}
		}
		return ingredients;
	}

	private static boolean deny(Player player, Level level, ItemStack stack) {
		if (!isBlocked(player, level, stack)) {
			return false;
		}
		tellBlocked(player);
		return true;
	}

	private static void tell(ServerPlayer player) {
		long now = player.level().getGameTime();
		Long next = nextMessageAt.get(player.getUUID());
		if (next != null && now < next) {
			return;
		}
		nextMessageAt.put(player.getUUID(), now + 20L);
		player.sendSystemMessage(DENIED);
	}
}
