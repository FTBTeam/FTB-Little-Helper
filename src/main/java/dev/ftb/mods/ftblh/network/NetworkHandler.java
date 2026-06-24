package dev.ftb.mods.ftblh.network;

import dev.ftb.mods.ftblh.FTBLittleHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
            .named(FTBLittleHelper.id("main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    private static int det = 0;

    private static int nextId() {
        return det++;
    }

    public static void init() {
        registerMessage(PacketToggleHelper.class, PacketToggleHelper::write, PacketToggleHelper::new, PacketToggleHelper::handle, NetworkDirection.PLAY_TO_SERVER);
    }

    public static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        NETWORK.registerMessage(nextId(), messageType, encoder, decoder, messageConsumer);
    }

    public static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, NetworkDirection direction) {
        NETWORK.registerMessage(nextId(), messageType, encoder, decoder, messageConsumer, Optional.of(direction));
    }
}
