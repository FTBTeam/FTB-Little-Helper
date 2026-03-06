package dev.ftb.mods.ftblh.entity.op;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public enum QueuedOperationType {
    NONE(NoOp::instance),
    MESSAGE(MessageOp::fromNetwork),
    SOUND(SoundOp::fromNetwork),
    COMMAND(CommandOp::fromNetwork);

    private final Function<FriendlyByteBuf, ? extends QueuedOperation> factory;

    QueuedOperationType(Function<FriendlyByteBuf, ? extends QueuedOperation> factory) {
        this.factory = factory;
    }

    public QueuedOperation fromNetwork(FriendlyByteBuf buf) {
        return factory.apply(buf);
    }
}
