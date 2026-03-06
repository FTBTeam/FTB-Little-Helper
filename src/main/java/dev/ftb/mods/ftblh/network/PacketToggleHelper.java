package dev.ftb.mods.ftblh.network;

import dev.ftb.mods.ftblh.LittleHelperBindings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketToggleHelper {
    public PacketToggleHelper(FriendlyByteBuf ignoredBuf) {
    }

    public PacketToggleHelper() {
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> LittleHelperBindings.INSTANCE.toggle(contextSupplier.get().getSender()));
    }
}
