package dev.ftb.mods.ftblh.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import dev.ftb.mods.ftblh.FTBLittleHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(FTBLittleHelper.MOD_ID)
public final class FTBLittleHelperForge {
    public FTBLittleHelperForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(FTBLittleHelper.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        FTBLittleHelper.init();

        if (FMLEnvironment.dist.isClient()) {
            ClientSetup.onModConstruction();
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerAttributes);

        MinecraftForge.EVENT_BUS.addListener(this::onEntityMount);
    }

    private void onEntityMount(EntityMountEvent event) {
        if (event.getEntityMounting() instanceof LittleHelperEntity) {
            event.setCanceled(true);
        }
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.LITTLE_HELPER.get(), LittleHelperEntity.createAttributes().build());
    }
}
