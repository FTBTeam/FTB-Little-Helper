package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

// commandception yay
public class CommandCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("command")
                .requires(s -> s.hasPermission(2))
                .then(argument("player", EntityArgument.player())
                        .then(argument("ticks", IntegerArgumentType.integer(-150, 150))
                                .then(argument("command", StringArgumentType.string())
                                        .executes(ctx -> queueCommand(ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player"),
                                                StringArgumentType.getString(ctx, "command"),
                                                IntegerArgumentType.getInteger(ctx, "ticks"),
                                                false)
                                        )
                                        .then(argument("silent", BoolArgumentType.bool())
                                                .executes(ctx -> queueCommand(ctx.getSource(),
                                                        EntityArgument.getPlayer(ctx, "player"),
                                                        StringArgumentType.getString(ctx, "command"),
                                                        IntegerArgumentType.getInteger(ctx, "ticks"),
                                                        BoolArgumentType.getBool(ctx, "silent"))
                                                )
                                        )
                                )
                        )
                );
    }

    private static int queueCommand(CommandSourceStack source, ServerPlayer target, String command, int ticks, boolean silent) throws CommandSyntaxException {
        return CommandUtil.getLittleHelper(source, target).map(helper -> {
            boolean ok = ticks < 0 ? helper.addPriorityCommand(command, silent, -ticks) : helper.addCommand(command, silent, ticks);
            return ok ? Command.SINGLE_SUCCESS : 0;
        }).orElse(0);
    }
}
