package dev.ftb.mods.ftblh;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.ftb.mods.ftblh.commands.CommandUtil;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import dev.ftb.mods.ftblh.registry.ModEntityTypes;
import net.minecraft.network.syncher.EntityDataSerializers;

public final class FTBLittleHelper {
    public static final String MOD_ID = "ftblh";

    public static void init() {
        CommandRegistrationEvent.EVENT.register(CommandUtil::registerCommands);

        ModEntityTypes.register();

        EntityDataSerializers.registerSerializer(LittleHelperEntity.TimedMessage.SERIALIZER);
    }
}
