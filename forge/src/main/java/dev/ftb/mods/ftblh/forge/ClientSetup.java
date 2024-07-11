package dev.ftb.mods.ftblh.forge;

import dev.ftb.mods.ftblh.FTBLittleHelper;
import dev.ftb.mods.ftblh.client.render.LittleHelperModel;
import dev.ftb.mods.ftblh.client.render.LittleHelperRenderer;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientSetup {
    public static final ModelLayerLocation LITTLE_HELPER_LAYER
            = new ModelLayerLocation(new ResourceLocation(FTBLittleHelper.MOD_ID, "little_helper"), "main");

    public static void onModConstruction() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::registerLayerDefinitions);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::registerRenderers);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LITTLE_HELPER_LAYER, LittleHelperModel::createBodyLayer);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.LITTLE_HELPER.get(), LittleHelperRenderer::new);
    }
}
