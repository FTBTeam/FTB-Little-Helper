package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MessageCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("message")
                .then(argument("player", EntityArgument.player())
                        .then(argument("ticks", IntegerArgumentType.integer(-150, 150))
                                .then(argument("message", ComponentArgument.textComponent())
                                        .executes(ctx -> message(ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player"),
                                                ComponentArgument.getComponent(ctx, "message"),
                                                IntegerArgumentType.getInteger(ctx, "ticks"))
                                        )
                                )
                        )
                );
    }

    public static int message(CommandSourceStack source, ServerPlayer target, Component msg, int ticks) throws CommandSyntaxException {
        CommandUtil.checkPermissions(source, target);

        if (msg.getContents() instanceof LiteralContents l && l.text().startsWith("#")) {
            // convenience notation for simple translations
            msg = Component.translatable(l.text().substring(1));
        }
        final var msg2 = msg;

        return CommandUtil.getLittleHelper(source, target).map(helper -> {
            boolean ok = ticks < 0 ? helper.addPriorityMessage(msg2, -ticks) : helper.addMessage(msg2, ticks);
            return ok ? Command.SINGLE_SUCCESS : 0;
        }).orElse(0);
    }
}
