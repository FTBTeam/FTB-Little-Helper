package dev.ftb.mods.ftblh.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.ftb.mods.ftblh.FTBLittleHelper;

public interface NetworkHandler {
    SimpleNetworkManager NET = SimpleNetworkManager.create(FTBLittleHelper.MOD_ID);

    MessageType TOGGLE_HELPER = NET.registerC2S("toggle_helper", PacketToggleHelper::new);
}
