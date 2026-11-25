package com.bych.command;

import com.bych.BychRuntime;
import com.bych.KingdomOfBych;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

/**
 * Zkušební příkazy. Vyžadují cheaty, tedy oprávnění 2.
 * /kbych reload znovu přečte ovládací soubor, hra se kvůli tomu nemusí zavírat.
 * /kbych status řekne den a dobu.
 * /kbych setday skočí na daný den, aby se epochy daly zkusit bez čekání.
 */
public final class BychCommands {
	private BychCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(Commands.literal("kbych")
						.requires(source -> source.hasPermission(2))
						.then(Commands.literal("reload").executes(BychCommands::reload))
						.then(Commands.literal("status").executes(BychCommands::status))
						.then(Commands.literal("setday")
								.then(Commands.argument("day", IntegerArgumentType.integer(1))
										.executes(BychCommands::setDay))))
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
		KingdomOfBych.LOGGER.info("Příkaz /kbych status: {}", text.getString());
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
		KingdomOfBych.LOGGER.info("Příkaz /kbych setday {}: {}", day, text.getString());
		// Hráči už větu dostali. Konzole hráč není, proto ji řekneme zvlášť, ať ji neřekneme dvakrát.
		if (context.getSource().getEntity() == null) {
			context.getSource().sendSuccess(() -> text, false);
		}
		return Command.SINGLE_SUCCESS;
	}
}
