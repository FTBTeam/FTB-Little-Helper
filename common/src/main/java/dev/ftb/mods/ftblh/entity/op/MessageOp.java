package dev.ftb.mods.ftblh.entity.op;

import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public record MessageOp(Component component) implements QueuedOperation {
    public static MessageOp fromNetwork(FriendlyByteBuf buf) {
        return new MessageOp(buf.readComponent());
    }

    @Override
    public void execute(LittleHelperEntity helper, boolean clientSide) {
        if (!clientSide && helper.isChatMessages()) {
            helper.getOwner().ifPresent(owner -> owner.displayClientMessage(Component.empty()
                    .append(Component.translatable("ftblh.chat_prefix").withStyle(ChatFormatting.YELLOW))
                    .append(component), false)
            );
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeComponent(component);
    }

    @Override
    public QueuedOperationType getType() {
        return QueuedOperationType.MESSAGE;
    }

    @Override
    public QueuedOperation copy() {
        return new MessageOp(component.copy());
    }

    @Override
    public boolean shouldSquash(LittleHelperEntity helper) {
        return helper.lastOp()
                .map(op -> op instanceof MessageOp m && m.component.equals(component))
                .orElse(false);
    }

    @Override
    public Component displayedMessage() {
        return component;
    }
}
