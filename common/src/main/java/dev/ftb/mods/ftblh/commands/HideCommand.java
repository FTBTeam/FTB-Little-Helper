package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.PersistedHelpers;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class HideCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("hide")
                .executes(ctx -> hide(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                .then(argument("player", EntityArgument.player())
                        .executes(ctx -> hide(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))
                );
    }

    public static int hide(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        CommandUtil.checkPermissions(source, target);

        if (HelperTracker.INSTANCE.getActiveHelper(target).isPresent()) {
            return CommandUtil.getLittleHelper(source, target).map(helper -> {
                helper.setStaysShown(false);
                helper.addMessage(Component.translatable("ftblh.message.bye").withStyle(ChatFormatting.YELLOW), 35);
                PersistedHelpers.getInstance(source.getServer()).checkAndRemove(target);
                return Command.SINGLE_SUCCESS;
            }).orElse(0);
        }

        return 0;
    }

}
