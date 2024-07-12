package dev.ftb.mods.ftblh.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class FTBLittleHelperClient {
    public static KeyMapping KEY_TOGGLE;

    public static void init() {
        KeyMappingRegistry.register(KEY_TOGGLE = new KeyMapping(
                "key.ftblh.toggle", InputConstants.Type.KEYSYM,
                -1, "key.categories.ftblh")
        );

        ClientTickEvent.CLIENT_PRE.register(FTBLittleHelperClient::onClientTick);
    }

    private static void onClientTick(Minecraft minecraft) {
        if (minecraft.player != null && KEY_TOGGLE.consumeClick()) {

        }
    }
}
