package dev.ftb.mods.ftblh.entity.op;

import dev.ftb.mods.ftblh.client.FTBLittleHelperClient;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public record SoundOp(SoundEvent soundEvent, SoundSource source, float volume, float pitch) implements QueuedOperation {

    public static SoundOp fromNetwork(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getHolder(id).orElse(SoundEvents.UI_BUTTON_CLICK).value();
        SoundSource source = buffer.readEnum(SoundSource.class);
        float volume = buffer.readFloat();
        float pitch = buffer.readFloat();
        return new SoundOp(soundEvent, source, volume, pitch);
    }

    @Override
    public void execute(LittleHelperEntity helper, boolean clientSide) {
        if (clientSide) {
            FTBLittleHelperClient.playSound(this);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(BuiltInRegistries.SOUND_EVENT.getId(soundEvent));
        buf.writeEnum(source);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }

    @Override
    public QueuedOperationType getType() {
        return QueuedOperationType.SOUND;
    }

    @Override
    public QueuedOperation copy() {
        return new SoundOp(soundEvent, source, volume, pitch);
    }
}
