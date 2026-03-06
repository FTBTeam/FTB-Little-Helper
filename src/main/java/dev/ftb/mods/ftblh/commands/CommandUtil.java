package dev.ftb.mods.ftblh.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.ftb.mods.ftblh.FTBLittleHelper;
import dev.ftb.mods.ftblh.HelperTracker;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Optional;

public class CommandUtil {
    public static final SimpleCommandExceptionType NO_PERMISSION
            = new SimpleCommandExceptionType(Component.translatable("ftblh.message.no_permission").withStyle(ChatFormatting.RED));

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(FTBLittleHelper.MOD_ID)
                .then(ShowCommand.register())
                .then(HideCommand.register())
                .then(MessageCommand.register())
                .then(SoundCommand.register())
                .then(CommandCommand.register())
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
            entity = createHelperForPlayer(source, target);
        } else {
            // helper is (or should be!) up
            entity = target.level().getEntity(id);
            if (entity == null) {
                // entity got lost somehow? create a new one (if previous helper became unloaded, it will be discarded next time it loads & ticks)
                FTBLittleHelper.LOGGER.warn("helper entity id {} for player {} got lost? creating a new one", id, target.getUUID());
                entity = createHelperForPlayer(source, target);
            }
        }
        if (entity instanceof LittleHelperEntity lh && lh.isAlive()) {
            return Optional.of(lh);
        }
        source.sendFailure(Component.literal("can't seem to get helper entity for target player!"));
        return Optional.empty();
    }

    public static LittleHelperEntity recreateHelper(CommandSourceStack source, ServerPlayer target) {
        // for players changing dimension, or dying
        try {
            HelperTracker.INSTANCE.unregister(target.getUUID());
            Entity entity = createHelperForPlayer(source, target);
            if (entity instanceof LittleHelperEntity helper) {
                return helper;
            }
        } catch (CommandSyntaxException e) {
            return null;
        }
        return null;
    }

    private static Entity createHelperForPlayer(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        Vec3 spawnPos = LittleHelperEntity.DEFAULT_POSITIONER.apply(target);
        Entity e = SummonCommand.createEntity(source, ModEntityTypes.LITTLE_HELPER.get().builtInRegistryHolder(), spawnPos, new CompoundTag(), false);
        if (e instanceof LittleHelperEntity helper) {
            helper.setOwner(target);
            helper.setStaysShown(true);
        }
        return e;
    }
}
