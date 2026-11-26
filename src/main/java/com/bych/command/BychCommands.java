package com.bych.command;

import java.util.ArrayList;
import java.util.List;

import com.bych.BychRuntime;
import com.bych.KingdomOfBych;
import com.bych.epoch.AgeTarget;
import com.bych.epoch.EpochClock;
import com.bych.epoch.PlaytimeStore;
import com.bych.game.Lobby;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * Zkušební příkazy. Vyžadují cheaty, tedy oprávnění 2.
 * Skupiny: time (status, setday, setage, night), test (reset), config (reload), game (depart).
 * Staré krátké tvary (/kbych status, setday, setage, night, reset, reload) zůstávají
 * kvůli command blokům ve světě.
 */
public final class BychCommands {
	private static final SuggestionProvider<CommandSourceStack> AGE_SUGGESTIONS = (context, builder) ->
			SharedSuggestionProvider.suggest(
					java.util.Arrays.stream(AgeTarget.values()).map(AgeTarget::commandName),
					builder
			);

	private BychCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(Commands.literal("kbych")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("time")
								.then(Commands.literal("status").executes(BychCommands::status))
								.then(Commands.literal("setday")
										.then(Commands.argument("day", IntegerArgumentType.integer(1))
												.executes(BychCommands::setDay)))
								.then(Commands.literal("setage")
										.then(Commands.argument("age", StringArgumentType.word())
												.suggests(AGE_SUGGESTIONS)
												.executes(BychCommands::setAge)))
								.then(Commands.literal("night").executes(BychCommands::night)))
						.then(Commands.literal("test")
								.then(Commands.literal("reset").executes(BychCommands::reset)))
						.then(Commands.literal("config")
								.then(Commands.literal("reload").executes(BychCommands::reload)))
						.then(Commands.literal("game")
								.then(Commands.literal("depart").executes(BychCommands::depart)))
						.then(Commands.literal("reload").executes(BychCommands::reload))
						.then(Commands.literal("status").executes(BychCommands::status))
						.then(Commands.literal("setday")
								.then(Commands.argument("day", IntegerArgumentType.integer(1))
										.executes(BychCommands::setDay)))
						.then(Commands.literal("setage")
								.then(Commands.argument("age", StringArgumentType.word())
										.suggests(AGE_SUGGESTIONS)
										.executes(BychCommands::setAge)))
						.then(Commands.literal("reset").executes(BychCommands::reset))
						.then(Commands.literal("night").executes(BychCommands::night)))
		);
	}

	private static int depart(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		Lobby.depart(context.getSource().getServer());
		if (context.getSource().getEntity() == null) {
			context.getSource().sendSuccess(() -> Component.literal("Odlet spuštěn."), false);
		}
		return Command.SINGLE_SUCCESS;
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
		ServerPlayer player = context.getSource().getPlayer();
		if (player == null) {
			context.getSource().sendFailure(Component.literal(
					"Status doby je u každého hráče jiný. Spusť příkaz ve hře jako hráč."
			));
			return 0;
		}
		Component text = BychRuntime.get().announcer().describe(player);
		KingdomOfBych.LOGGER.info("Příkaz /kbych time status: {}", text.getString());
		context.getSource().sendSuccess(() -> text, false);
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * Staré setday z command bloků. Číslo dne mapuje na dobu hráče:
	 * 1 = kámen, 2–4 = bronz, od 5 = železo. Den světa se taky posune.
	 * Command blok nemá hráče → platí všem online.
	 */
	private static int setDay(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		int day = IntegerArgumentType.getInteger(context, "day");
		AgeTarget age = ageFromLegacyDay(day);
		MinecraftServer server = context.getSource().getServer();
		BychRuntime.get().clock().applyDay(server.overworld(), day);
		int changed = applyAgeToTargets(context.getSource(), age);
		Component text = Component.literal(
				"Nastaveno: " + age.czechName() + " (setday " + day + ", hráčů " + changed + ")."
		);
		KingdomOfBych.LOGGER.info("Příkaz /kbych time setday {}: {}", day, text.getString());
		context.getSource().sendSuccess(() -> text, true);
		return Command.SINGLE_SUCCESS;
	}

	private static int setAge(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		String raw = StringArgumentType.getString(context, "age");
		AgeTarget age = AgeTarget.fromCommandName(raw);
		if (age == null) {
			context.getSource().sendFailure(Component.literal(
					"Neznámá doba. Použij stone, bronze, iron nebo medieval."
			));
			return 0;
		}
		int changed = applyAgeToTargets(context.getSource(), age);
		if (changed == 0) {
			context.getSource().sendFailure(Component.literal(
					"Nikdo online. setage potřebuje hráče ve světě."
			));
			return 0;
		}
		Component text = Component.literal("Nastaveno: " + age.czechName() + " (hráčů " + changed + ").");
		KingdomOfBych.LOGGER.info("Příkaz /kbych time setage {}: {}", age.commandName(), text.getString());
		context.getSource().sendSuccess(() -> text, true);
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * Hráč příkazu → jen on. Command blok / konzole → všichni online (jako dřív u setday).
	 */
	private static int applyAgeToTargets(CommandSourceStack source, AgeTarget age) {
		MinecraftServer server = source.getServer();
		PlaytimeStore store = PlaytimeStore.get(server);
		BychRuntime runtime = BychRuntime.get();
		List<ServerPlayer> targets = new ArrayList<>();
		ServerPlayer self = source.getPlayer();
		if (self != null) {
			targets.add(self);
		} else {
			targets.addAll(server.getPlayerList().getPlayers());
		}
		for (ServerPlayer player : targets) {
			runtime.clock().applyAge(player, age, store, runtime.config());
			runtime.announcer().tellAfterAgeChange(player);
		}
		return targets.size();
	}

	/**
	 * Stejné rozmezí jako dřívější dny světa: 1 kámen, 2–4 bronz, od 5 železo.
	 * Ať staré command bloky se setday pořád přepínají dobu.
	 */
	private static AgeTarget ageFromLegacyDay(int day) {
		if (day <= 1) {
			return AgeTarget.STONE;
		}
		if (day <= 4) {
			return AgeTarget.BRONZE;
		}
		return AgeTarget.IRON;
	}

	/**
	 * Vyčistí inventář všem připojeným hráčům, vynuluje jejich playtime a vrátí den světa na 1.
	 * Command block nemá hráče, proto se čistí všichni, kdo jsou ve světě.
	 */
	private static int reset(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
		MinecraftServer server = context.getSource().getServer();
		PlaytimeStore store = PlaytimeStore.get(server);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.getInventory().clearContent();
			player.containerMenu.setCarried(ItemStack.EMPTY);
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				player.setItemSlot(slot, ItemStack.EMPTY);
			}
			store.resetPlayer(player.getUUID());
			BychRuntime.get().announcer().tellAfterAgeChange(player);
		}
		ServerLevel overworld = server.overworld();
		BychRuntime.get().clock().applyDay(overworld, 1);
		Component text = Component.literal("Inventář je prázdný. Doba kamenná, odehraný čas vynulovaný.");
		KingdomOfBych.LOGGER.info(text.getString());
		context.getSource().sendSuccess(() -> text, true);
		return Command.SINGLE_SUCCESS;
	}

	/**
	 * Noc aktuálního dne světa. Na dobu hráče nemá vliv.
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
