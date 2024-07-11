package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.ftb.mods.ftblh.HelperTracker;
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

    public static int hide(CommandSourceStack source, ServerPlayer target) {
        int id = HelperTracker.INSTANCE.getHelperId(target.getUUID());

        if (id > 0) {
            Entity entity = target.level().getEntity(id);
            if (entity instanceof LittleHelperEntity lh && lh.isAlive()) {
                lh.setStaysShown(false);
                lh.addMessage(Component.literal("Bye!").withStyle(ChatFormatting.YELLOW), 35);
                source.sendSuccess(() -> Component.literal("hid helper for " + target.getGameProfile().getName()), false);
            } else {
                source.sendFailure(Component.literal("helper entity " + id + " for player is already hidden?"));
            }
        } else {
            source.sendFailure(Component.literal("target player does not have active helper entity"));
        }
        
        return 0;
    }
}
