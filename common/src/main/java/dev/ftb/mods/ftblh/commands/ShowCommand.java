package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.PersistedHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class ShowCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("show")
                .executes(ctx -> show(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> show(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
                );
    }

    public static int show(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        CommandUtil.checkPermissions(source, target);

        if (HelperTracker.INSTANCE.getActiveHelper(target).isEmpty()) {
            return CommandUtil.getLittleHelper(source, target).map(helper -> {
                PersistedHelpers.getInstance(source.getServer()).add(target);
                helper.setStaysShown(true);
                helper.addMessage(Component.translatable("ftblh.message.hi").withStyle(ChatFormatting.YELLOW), 35);
                return Command.SINGLE_SUCCESS;
            }).orElse(0);
        }
        return 0;
    }

}
