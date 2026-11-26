package com.bych.epoch;

import java.util.List;
import java.util.Locale;

import com.bych.BychRuntime;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Co se v které době smí vyrobit a použít.
 * Kůže, tlačítka, křesadlo, ohniště a nalezený trident jsou povolené pořád.
 * Středověk ještě nikdo nemá. Až půjde založit vesnici, odemkne se jen tomu hráči.
 * Do té doby nejde zlato, diamant, netherit ani redstone.
 */
public final class CraftPolicy {
	private CraftPolicy() {
	}

	/**
	 * Vrátí důvod zákazu, nebo prázdný řetězec když je věc povolená.
	 * Suroviny v mřížce se počítají taky, aby minecart z železa neprošel jen proto,
	 * že v názvu nemá slovo iron.
	 */
	public static String blockReason(ServerPlayer player, ItemStack result, List<ItemStack> ingredients) {
		if (result.isEmpty()) {
			return "";
		}
		if (isAlwaysAllowed(result)) {
			return "";
		}
		Epoch epoch = epochOf(player);
		boolean medieval = hasMedieval(player);
		String found = forbiddenMaterial(epoch, medieval, result);
		if (!found.isEmpty()) {
			return found;
		}
		for (ItemStack ingredient : ingredients) {
			if (ingredient.isEmpty() || isAlwaysAllowed(ingredient)) {
				continue;
			}
			found = forbiddenMaterial(epoch, medieval, ingredient);
			if (!found.isEmpty()) {
				return "ingredient:" + found;
			}
		}
		if (epoch == Epoch.STONE && isFurnace(result)) {
			return "furnace-stone";
		}
		if (epoch == Epoch.STONE && result.is(Items.SADDLE)) {
			return "saddle-stone";
		}
		return "";
	}

	/** Středověk je na hráče. Vesnice ještě neodemykají; setage ho umí zapnout na zkoušku. */
	public static boolean hasMedieval(ServerPlayer player) {
		return PlaytimeStore.get(player.getServer()).of(player.getUUID()).medieval();
	}

	public static Epoch epochOf(ServerPlayer player) {
		BychRuntime runtime = BychRuntime.get();
		PlaytimeStore store = PlaytimeStore.get(player.getServer());
		return runtime.clock().epochOf(player, store, runtime.config());
	}

	private static boolean isAlwaysAllowed(ItemStack stack) {
		if (stack.is(ItemTags.BUTTONS) || stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.TRIDENT)) {
			return true;
		}
		if (stack.is(Items.CAMPFIRE) || stack.is(Items.SOUL_CAMPFIRE)) {
			return true;
		}
		String path = path(stack);
		return path.contains("leather") || path.equals("item_frame") || path.equals("glow_item_frame");
	}

	private static boolean isFurnace(ItemStack stack) {
		return stack.is(Items.FURNACE) || stack.is(Items.BLAST_FURNACE)
				|| stack.is(Items.SMOKER) || stack.is(Items.FURNACE_MINECART);
	}

	/**
	 * Který zakázaný materiál na věci je, nebo prázdný řetězec.
	 * Železo je zakázané v kameni a bronzu. Měď a bronz jen v kameni.
	 * Redstone, zlato, diamant a netherit až do středověku.
	 */
	private static String forbiddenMaterial(Epoch epoch, boolean medieval, ItemStack stack) {
		String kind = materialKind(stack);
		if (kind.isEmpty()) {
			return "";
		}
		boolean metalLockedUntilMedieval = kind.equals("gold") || kind.equals("diamond") || kind.equals("netherite");
		if (!medieval && metalLockedUntilMedieval) {
			return kind;
		}
		if (!medieval && kind.equals("redstone")) {
			return kind;
		}
		if ((epoch == Epoch.STONE || epoch == Epoch.BRONZE) && kind.equals("iron")) {
			return kind;
		}
		if (epoch == Epoch.STONE && (kind.equals("copper") || kind.equals("bronze"))) {
			return kind;
		}
		return "";
	}

	private static String materialKind(ItemStack stack) {
		String path = path(stack);
		if (path.contains("netherite")) {
			return "netherite";
		}
		if (path.contains("diamond")) {
			return "diamond";
		}
		if (path.contains("gold") || path.contains("golden") || path.equals("clock")) {
			return "gold";
		}
		if (path.contains("copper") || path.equals("lightning_rod")) {
			return "copper";
		}
		if (path.contains("bronze")) {
			return "bronze";
		}
		if (path.contains("redstone") || isRedstonePart(path)) {
			return "redstone";
		}
		if (path.contains("iron") || isIronProduct(path)) {
			return "iron";
		}
		return "";
	}

	private static boolean isRedstonePart(String path) {
		return path.equals("repeater") || path.equals("comparator")
				|| path.equals("piston") || path.equals("sticky_piston")
				|| path.equals("observer") || path.equals("dispenser") || path.equals("dropper")
				|| path.equals("hopper") || path.equals("compass")
				|| path.equals("daylight_detector") || path.equals("lever")
				|| path.equals("tripwire_hook") || path.equals("note_block")
				|| path.equals("target") || path.equals("crafter")
				|| path.equals("powered_rail") || path.equals("activator_rail") || path.equals("detector_rail")
				|| path.contains("pressure_plate");
	}

	/** Věci ze železa, v jejichž názvu slovo iron není. */
	private static boolean isIronProduct(String path) {
		return path.equals("minecart") || path.endsWith("_minecart")
				|| path.equals("bucket") || path.endsWith("_bucket")
				|| path.equals("shears") || path.equals("cauldron")
				|| path.equals("chain") || path.equals("lantern") || path.equals("soul_lantern")
				|| path.equals("anvil") || path.equals("chipped_anvil") || path.equals("damaged_anvil")
				|| path.equals("stonecutter") || path.equals("smithing_table")
				|| path.equals("crossbow")
				|| path.equals("rail");
	}

	private static String path(ItemStack stack) {
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase(Locale.ROOT);
	}
}
