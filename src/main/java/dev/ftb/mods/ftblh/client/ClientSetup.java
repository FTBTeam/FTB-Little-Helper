package dev.ftb.mods.ftblh.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ftb.mods.ftblh.FTBLittleHelper;
import dev.ftb.mods.ftblh.client.render.LittleHelperModel;
import dev.ftb.mods.ftblh.client.render.LittleHelperRenderer;
import dev.ftb.mods.ftblh.entity.op.SoundOp;
import dev.ftb.mods.ftblh.network.NetworkHandler;
import dev.ftb.mods.ftblh.network.PacketToggleHelper;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ClientSetup {
    public static KeyMapping KEY_TOGGLE;

    public static final ModelLayerLocation LITTLE_HELPER_LAYER
            = new ModelLayerLocation(new ResourceLocation(FTBLittleHelper.MOD_ID, "little_helper"), "main");

    public static void onModConstruction(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerLayerDefinitions);
        modBus.addListener(ClientSetup::registerRenderers);
        modBus.addListener(ClientSetup::registerKeyMappings);

        MinecraftForge.EVENT_BUS.addListener(ClientSetup::onClientTick);
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().player != null && KEY_TOGGLE.consumeClick()) {
                NetworkHandler.NETWORK.sendToServer(new PacketToggleHelper());
            }
        }
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LITTLE_HELPER_LAYER, LittleHelperModel::createBodyLayer);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.LITTLE_HELPER.get(), LittleHelperRenderer::new);
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KEY_TOGGLE = new KeyMapping("key.ftblh.toggle", InputConstants.Type.KEYSYM, -1, "key.categories.ftblh");

        event.register(KEY_TOGGLE);
    }

    public static void playSound(SoundOp sound) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.playNotifySound(sound.soundEvent(), sound.source(), sound.volume(), sound.pitch());
        }
    }
}
