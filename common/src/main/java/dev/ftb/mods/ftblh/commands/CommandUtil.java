package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ftb.mods.ftblh.FTBLittleHelper;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class CommandUtil {
    public static final SimpleCommandExceptionType NO_PERMISSION
            = new SimpleCommandExceptionType(Component.translatable("ftblh.message.no_permission").withStyle(ChatFormatting.RED));

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(FTBLittleHelper.MOD_ID)
                .then(ShowCommand.register())
                .then(HideCommand.register())
                .then(MessageCommand.register())
                .then(SoundCommand.register())
        );
    }

    static void checkPermissions(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        if (source.getPlayer() != target && !source.hasPermission(Commands.LEVEL_GAMEMASTERS)) {
            throw NO_PERMISSION.create();
        }
    }

    static Optional<LittleHelperEntity> getLittleHelper(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        int id = HelperTracker.INSTANCE.getHelperId(target.getUUID());

        Entity entity;
        if (id == 0) {
            // no helper right now, raise it
            Vec3 spawnPos = LittleHelperEntity.DEFAULT_POSITIONER.apply(target);
            entity = SummonCommand.createEntity(source, ModEntityTypes.LITTLE_HELPER.get().builtInRegistryHolder(), spawnPos, new CompoundTag(), false);
        } else {
            // helper is (or should be!) up
            entity = target.level().getEntity(id);
        }
        if (entity instanceof LittleHelperEntity lh && lh.isAlive()) {
            if (lh.getOwner().isEmpty()) {
                lh.setOwner(target);
            }
            return Optional.of(lh);
        }
        source.sendFailure(Component.literal("can't seem to get helper entity for target player!"));
        return Optional.empty();
    }

    public static LittleHelperEntity recreateHelper(CommandSourceStack source, ServerPlayer target) {
        // needed for players changing dimension, or dying
        Vec3 spawnPos = LittleHelperEntity.DEFAULT_POSITIONER.apply(target);
        try {
            Entity entity = SummonCommand.createEntity(source, ModEntityTypes.LITTLE_HELPER.get().builtInRegistryHolder(), spawnPos, new CompoundTag(), false);
            if (entity instanceof LittleHelperEntity helper) {
                helper.setOwner(target);
                helper.setStaysShown(true);
                return helper;
            }
        } catch (CommandSyntaxException e) {
            return null;
        }
        return null;
    }
}
