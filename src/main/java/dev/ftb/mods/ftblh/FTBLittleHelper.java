package dev.ftb.mods.ftblh;

import dev.ftb.mods.ftblh.client.ClientSetup;
import dev.ftb.mods.ftblh.commands.CommandUtil;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.entity.op.TimedOperation;
import dev.ftb.mods.ftblh.network.NetworkHandler;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FTBLittleHelper.MOD_ID)
public final class FTBLittleHelper {
    public static final String MOD_ID = "ftblh";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FTBLittleHelper() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        if (FMLEnvironment.dist.isClient()) {
            ClientSetup.onModConstruction(modBus);
        }

        modBus.addListener(FTBLittleHelper::registerAttributes);
        modBus.addListener(FTBLittleHelper::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onEntityMount);
        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onEntityTeleport);
        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onDimensionChange);
        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onPlayerRespawn);
        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(FTBLittleHelperEventHandler::onServerStop);
        MinecraftForge.EVENT_BUS.addListener(CommandUtil::registerCommands);

        EntityDataSerializers.registerSerializer(TimedOperation.SERIALIZER);

        ModEntityTypes.register(modBus);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.LITTLE_HELPER.get(), LittleHelperEntity.createAttributes().build());
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}
