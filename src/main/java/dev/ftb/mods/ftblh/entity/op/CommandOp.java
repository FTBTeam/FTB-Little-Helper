package dev.ftb.mods.ftblh.entity.op;

import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;

public record CommandOp(String command, boolean silent) implements QueuedOperation {
    public static QueuedOperation fromNetwork(FriendlyByteBuf buf) {
        return new CommandOp(buf.readUtf(), buf.readBoolean());
    }

    @Override
    public void execute(LittleHelperEntity helper, boolean clientSide) {
        if (!clientSide) {
            helper.getOwner().ifPresent(player -> {
                CommandSourceStack source = player.createCommandSourceStack().withPermission(2);
                if (silent) source = source.withSuppressedOutput();
                player.getServer().getCommands().performPrefixedCommand(source, command);
            });
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(command);
        buf.writeBoolean(silent);
    }

    @Override
    public QueuedOperationType getType() {
        return QueuedOperationType.COMMAND;
    }

    @Override
    public QueuedOperation copy() {
        return new CommandOp(command, silent);
    }
}
