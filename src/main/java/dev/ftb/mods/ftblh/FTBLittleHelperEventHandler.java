package dev.ftb.mods.ftblh;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftblh.commands.CommandUtil;
import dev.ftb.mods.ftblh.commands.ShowCommand;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;

public final class FTBLittleHelperEventHandler {
    public static void onServerStop(ServerStoppingEvent ignoredEvent) {
        HelperTracker.INSTANCE.clear();
    }

    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && sp.getServer() != null && PersistedHelpers.getInstance(sp.getServer()).checkAndRemove(sp)) {
            try {
                ShowCommand.show(sp.createCommandSourceStack(), sp, "");
            } catch (CommandSyntaxException e) {
                FTBLittleHelper.LOGGER.warn("couldn't bring little helper up for player {}", sp.getUUID());
            }
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.player instanceof ServerPlayer sp) {
            int id = HelperTracker.INSTANCE.getHelperId(sp.getUUID());
            if (id != 0 && sp.level().getEntity(id) instanceof LittleHelperEntity lh) {
                // if the helper gets too far away (player moving very fast, or teleporting), port it straight to the player
                if (lh.distanceToSqr(sp) > 1024) {
                    lh.setPos(lh.targetPosition(sp));
                }
            }
        }
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        // on dimension changed, discard the helper entity in the old dimension, and create a new one
        // TODO: copy over any pending messages?
        if (event.getEntity() instanceof ServerPlayer sp) {
            int id = HelperTracker.INSTANCE.getHelperId(sp.getUUID());

            Level oldLevel = sp.getServer().getLevel(event.getFrom());

            if (oldLevel != null && id != 0 && oldLevel.getEntity(id) instanceof LittleHelperEntity oldHelper) {
                oldHelper.discard();
                CommandUtil.recreateHelper(sp.createCommandSourceStack(), sp);
            }
        }
    }


    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered() && event.getEntity() instanceof ServerPlayer sp) {
            // when respawning after a death, recreate the helper if it was previously active
            // (if the player dies, little helper despawns if active, but player ID stays in the tracker)
            if (HelperTracker.INSTANCE.isRegistered(sp.getUUID())) {
                LittleHelperEntity helper = CommandUtil.recreateHelper(sp.createCommandSourceStack(), sp);
                if (helper != null) {
                    helper.addMessage(Component.translatable("ftblh.message.death_sad").withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }

    public static void onEntityTeleport(EntityTeleportEvent.TeleportCommand event) {
        if (event.getEntity() instanceof LittleHelperEntity) {
            event.setCanceled(true);
            FTBLittleHelper.LOGGER.debug("canceled teleportation command for little helper entity id {}", event.getEntity().getId());
        }
    }

    public static void onEntityMount(EntityMountEvent event) {
        if (event.getEntityMounting() instanceof LittleHelperEntity) {
            event.setCanceled(true);
        }
    }
}
