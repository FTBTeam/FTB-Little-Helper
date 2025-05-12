package dev.ftb.mods.ftblh.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftblh.LittleHelperBindings;
import net.minecraft.network.FriendlyByteBuf;

public class PacketToggleHelper extends BaseC2SMessage {
    public PacketToggleHelper(FriendlyByteBuf ignoredBuf) {
    }

    public PacketToggleHelper() {
    }

    @Override
    public MessageType getType() {
        return NetworkHandler.TOGGLE_HELPER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> LittleHelperBindings.INSTANCE.toggle(context.getPlayer()));
    }
}
