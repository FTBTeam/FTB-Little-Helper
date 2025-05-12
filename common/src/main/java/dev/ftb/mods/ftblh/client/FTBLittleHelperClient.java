package dev.ftb.mods.ftblh.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.ftb.mods.ftblh.SyncableSound;
import dev.ftb.mods.ftblh.network.NetworkHandler;
import dev.ftb.mods.ftblh.network.PacketToggleHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;

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
            new PacketToggleHelper().sendToServer();
        }
    }

    public static void playSound(SyncableSound sound) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.playNotifySound(sound.soundEvent(), sound.source(), sound.volume(), sound.pitch());
        }
    }
}
