package dev.ftb.mods.ftblh.entity.op;

import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public interface QueuedOperation {
    void execute(LittleHelperEntity helper, boolean clientSide);

    void write(FriendlyByteBuf buf);

    QueuedOperationType getType();

    QueuedOperation copy();

    default boolean shouldSquash(LittleHelperEntity helper) {
        return false;
    }

    default Component displayedMessage() {
        return Component.empty();
    }
}
