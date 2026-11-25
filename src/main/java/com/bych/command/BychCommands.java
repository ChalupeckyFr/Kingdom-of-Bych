package com.bych.command;

import com.bych.BychRuntime;
import com.bych.KingdomOfBych;
import com.bych.epoch.EpochClock;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Zkušební příkazy. Vyžadují cheaty, tedy oprávnění 2.
 * Skupiny: time (status, setday, night), test (reset), config (reload).
 * Staré krátké tvary (/kbych status, setday, night, reset, reload) zůstávají
 * kvůli command blokům ve světě.
 */
public final class BychCommands {
	private BychCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(Commands.literal("kbych")
						.requires(source -> source.hasPermission(2))
						// Nové skupiny
						.then(Commands.literal("time")
								.then(Commands.literal("status").executes(BychCommands::status))
								.then(Commands.literal("setday")
										.then(Commands.argument("day", IntegerArgumentType.integer(1))
												.executes(BychCommands::setDay)))
								.then(Commands.literal("night").executes(BychCommands::night)))
						.then(Commands.literal("test")
								.then(Commands.literal("reset").executes(BychCommands::reset)))
						.then(Commands.literal("config")
								.then(Commands.literal("reload").executes(BychCommands::reload)))
						// Staré krátké tvary — command bloky ve světě dál fungují
						.then(Commands.literal("reload").executes(BychCommands::reload))
						.then(Commands.literal("status").executes(BychCommands::status))
						.then(Commands.literal("setday")
								.then(Commands.argument("day", IntegerArgumentType.integer(1))
										.executes(BychCommands::setDay)))
						.then(Commands.literal("reset").executes(BychCommands::reset))
						.then(Commands.literal("night").executes(BychCommands::night)))
		);
	}

	private static int reload(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		BychRuntime runtime = BychRuntime.get();
		runtime.reloadConfig();
		runtime.applySleepRule(context.getSource().getServer());
		Component summary = runtime.config().summary();
		KingdomOfBych.LOGGER.info(summary.getString());
		context.getSource().sendSuccess(() -> summary, true);
		return Command.SINGLE_SUCCESS;
	}

	private static int status(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		MinecraftServer server = context.getSource().getServer();
		Component text = BychRuntime.get().announcer().describe(server.overworld());
		KingdomOfBych.LOGGER.info("Příkaz /kbych time status: {}", text.getString());
		context.getSource().sendSuccess(() -> text, false);
		return Command.SINGLE_SUCCESS;
	}

	private static int setDay(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		int day = IntegerArgumentType.getInteger(context, "day");
		MinecraftServer server = context.getSource().getServer();
		// Den patří hlavnímu světu. I kdyby příkaz napsal někdo v Netheru, mění se stejné hodiny.
		ServerLevel overworld = server.overworld();
		BychRuntime.get().clock().applyDay(overworld, day);
		BychRuntime.get().announcer().tellAfterSetDay(server);
		Component text = BychRuntime.get().announcer().describe(overworld);
		KingdomOfBych.LOGGER.info("Příkaz /kbych time setday {}: {}", day, text.getString());
		// Hráči už větu dostali. Konzole hráč není, proto ji řekneme zvlášť, ať ji neřekneme dvakrát.
		if (context.getSource().getEntity() == null) {
			context.getSource().sendSuccess(() -> text, false);
		}
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * Vyčistí inventář všem připojeným hráčům a vrátí svět na den 1.
	 * Command block nemá hráče, proto se čistí všichni, kdo jsou ve světě.
	 */
	private static int reset(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		MinecraftServer server = context.getSource().getServer();
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.getInventory().clearContent();
			player.containerMenu.setCarried(ItemStack.EMPTY);
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				player.setItemSlot(slot, ItemStack.EMPTY);
			}
		}
		ServerLevel overworld = server.overworld();
		BychRuntime.get().clock().applyDay(overworld, 1);
		BychRuntime.get().announcer().tellAfterSetDay(server);
		Component text = Component.literal("Inventář je prázdný. Doba kamenná, den 1.");
		KingdomOfBych.LOGGER.info(text.getString());
		context.getSource().sendSuccess(() -> text, true);
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * Noc aktuálního dne. Nepřepisuje čas na noc prvního dne.
	 * Den 2 má noc na ticku 24000 + 13000, ne na 13000.
	 */
	private static int night(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		ServerLevel overworld = context.getSource().getServer().overworld();
		long before = overworld.getDayTime();
		long dayIndex = Math.floorDiv(before, EpochClock.TICKS_PER_DAY);
		long nightTime = dayIndex * EpochClock.TICKS_PER_DAY + 13000L;
		overworld.setDayTime(nightTime);
		Component text = Component.literal("Noc tohoto dne.");
		KingdomOfBych.LOGGER.info("Příkaz /kbych time night: {} -> {}", before, nightTime);
		context.getSource().sendSuccess(() -> text, true);
		return Command.SINGLE_SUCCESS;
	}
}
