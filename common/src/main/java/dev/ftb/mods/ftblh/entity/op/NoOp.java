package dev.ftb.mods.ftblh.entity.op;

import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.network.FriendlyByteBuf;

public enum NoOp implements QueuedOperation {
    INSTANCE;

    public static QueuedOperation instance(FriendlyByteBuf ignoredBuf) {
        return INSTANCE;
    }

    @Override
    public void execute(LittleHelperEntity helper, boolean clientSide) {
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public QueuedOperationType getType() {
        return QueuedOperationType.NONE;
    }

    @Override
    public QueuedOperation copy() {
        return INSTANCE;
    }
}
