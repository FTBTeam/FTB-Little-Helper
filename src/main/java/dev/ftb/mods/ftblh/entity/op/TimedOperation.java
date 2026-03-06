package dev.ftb.mods.ftblh.entity.op;

import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record TimedOperation(QueuedOperation op, int duration) {
    public static final TimedOperation NONE = new TimedOperation(NoOp.INSTANCE, 0);

    public static final EntityDataSerializer<TimedOperation> SERIALIZER = new EntityDataSerializer.ForValueType<>() {
        @Override
        public void write(FriendlyByteBuf buf, TimedOperation object) {
            buf.writeEnum(object.op.getType());
            object.op.write(buf);
            buf.writeVarInt(object.duration);
        }

        @Override
        public TimedOperation read(FriendlyByteBuf buf) {
            QueuedOperationType type = buf.readEnum(QueuedOperationType.class);
            QueuedOperation message = type.fromNetwork(buf);
            int duration = buf.readVarInt();
            return new TimedOperation(message, duration);
        }

        @Override
        public TimedOperation copy(TimedOperation timedOperation) {
            return new TimedOperation(timedOperation.op.copy(), timedOperation.duration);
        }
    };

    public static TimedOperation ofComponent(Component message, int duration) {
        return new TimedOperation(new MessageOp(message), duration);
    }

    public static TimedOperation ofSound(SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, int duration) {
        return new TimedOperation(new SoundOp(soundEvent, soundSource, volume, pitch), duration);
    }

    public static TimedOperation ofCommand(String command, boolean silent, int duration) {
        return new TimedOperation(new CommandOp(command, silent), duration);
    }

    public boolean shouldSquash(LittleHelperEntity lh) {
        return op.shouldSquash(lh);
    }
}
