package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

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
        int id = HelperTracker.INSTANCE.getHelperId(target.getUUID());

        if (id == 0) {
            // no helper right now, raise it
            Vec3 spawnPos = target.getEyePosition().add(target.getLookAngle().normalize()).subtract(0.0, 0.4, 0.0);
            Entity entity = SummonCommand.createEntity(source, ModEntityTypes.LITTLE_HELPER.get().builtInRegistryHolder(), spawnPos, new CompoundTag(), false);
            if (entity instanceof LittleHelperEntity helper) {
                helper.setOwner(target);
                helper.setStaysShown(true);
                helper.addMessage(Component.literal("Hi!").withStyle(ChatFormatting.YELLOW), 35);
                source.sendSuccess(() -> Component.literal("showed helper for " + target.getGameProfile().getName()), false);
            } else {
                source.sendFailure(Component.literal("can't create a little helper entity"));
            }
        } else {
            source.sendFailure(Component.literal("target player already has helper entity? eid = " + id));
        }

        return 0;
    }
}
