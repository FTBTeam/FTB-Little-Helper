package dev.ftb.mods.ftblh;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ftb.mods.ftblh.client.FTBLittleHelperClient;
import dev.ftb.mods.ftblh.commands.CommandUtil;
import dev.ftb.mods.ftblh.commands.ShowCommand;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FTBLittleHelper {
    public static final String MOD_ID = "ftblh";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        CommandRegistrationEvent.EVENT.register(CommandUtil::registerCommands);
        PlayerEvent.PLAYER_JOIN.register(FTBLittleHelper::onPlayerJoin);
        PlayerEvent.CHANGE_DIMENSION.register(FTBLittleHelper::onDimensionChange);
        PlayerEvent.PLAYER_CLONE.register(FTBLittleHelper::onPlayerRespawn);
        TickEvent.PLAYER_PRE.register(FTBLittleHelper::onPlayerTick);
        LifecycleEvent.SERVER_STOPPING.register(FTBLittleHelper::onServerStop);

        ModEntityTypes.register();

        EntityDataSerializers.registerSerializer(LittleHelperEntity.TimedMessage.SERIALIZER);

        EnvExecutor.runInEnv(Env.CLIENT, () -> FTBLittleHelperClient::init);
    }

    private static void onServerStop(MinecraftServer minecraftServer) {
        HelperTracker.INSTANCE.clear();
    }

    private static void onPlayerJoin(ServerPlayer player) {
        if (player.getServer() != null && PersistedHelpers.getInstance(player.getServer()).checkAndRemove(player)) {
            try {
                ShowCommand.show(player.createCommandSourceStack(), player);
            } catch (CommandSyntaxException e) {
                FTBLittleHelper.LOGGER.warn("couldn't bring little helper up for player {}", player.getUUID());
            }
        }
    }

    private static void onPlayerTick(Player player) {
        if (player instanceof ServerPlayer sp) {
            int id = HelperTracker.INSTANCE.getHelperId(player.getUUID());
            if (id != 0 && sp.level().getEntity(id) instanceof LittleHelperEntity lh) {
                // if the helper gets too far away (player moving very fast, or teleporting), port it straight to the player
                if (lh.distanceToSqr(sp) > 1024) {
                    lh.setPos(lh.targetPosition(sp));
                }
            }
        }
    }

    private static void onDimensionChange(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> to) {
        // on dimension changed, discard the helper entity in the old dimension, and create a new one
        // TODO: copy over any pending messages?
        int id = HelperTracker.INSTANCE.getHelperId(player.getUUID());

        Level oldLevel = player.getServer().getLevel(from);

        if (oldLevel != null && id != 0 && oldLevel.getEntity(id) instanceof LittleHelperEntity oldHelper) {
            oldHelper.discard();
            HelperTracker.INSTANCE.unregister(player.getUUID());
            CommandUtil.recreateHelper(player.createCommandSourceStack(), player);
        }
    }


    private static void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) {
        if (!wonGame) {
            // when respawning after a death, recreate the helper if it was previously active
            // (if the player dies, little helper despawns if active, but player ID stays in the tracker)
            if (HelperTracker.INSTANCE.isRegistered(newPlayer.getUUID())) {
                HelperTracker.INSTANCE.unregister(newPlayer.getUUID());
                LittleHelperEntity helper = CommandUtil.recreateHelper(newPlayer.createCommandSourceStack(), newPlayer);
                if (helper != null) {
                    helper.addMessage(Component.translatable("ftblh.message.death_sad").withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(FTBLittleHelper.MOD_ID, name);
    }
}
