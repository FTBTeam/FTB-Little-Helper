package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MessageCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return literal("message")
                .then(argument("player", EntityArgument.player())
                        .then(argument("ticks", IntegerArgumentType.integer(10, 100))
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
        int id = HelperTracker.INSTANCE.getHelperId(target.getUUID());

        Entity entity;
        if (id == 0) {
            // no helper right now, raise it
            Vec3 spawnPos = target.getEyePosition().add(target.getLookAngle().normalize()).subtract(0.0, 0.5, 0.0);
            entity = SummonCommand.createEntity(source, ModEntityTypes.LITTLE_HELPER.get().builtInRegistryHolder(), spawnPos, new CompoundTag(), false);
        } else {
            // helper is (or should be!) up
            entity = target.level().getEntity(id);
        }

        if (entity instanceof LittleHelperEntity helper) {
            if (helper.getOwner().isEmpty()) {
                helper.setOwner(target);
            }
            boolean ok = helper.addMessage(msg, ticks);
            if (!ok) {
                source.sendFailure(Component.literal("too many message queued for player, slow down!"));
                return 0;
            } else {
                return 1;
            }
        } else {
            source.sendFailure(Component.literal("can't seem to get helper entity for target player!"));
            return 0;
        }
    }
}
