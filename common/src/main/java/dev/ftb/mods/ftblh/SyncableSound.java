package dev.ftb.mods.ftblh;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public record SyncableSound(SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
    public static SyncableSound fromNetwork(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getHolder(id).orElse(SoundEvents.UI_BUTTON_CLICK).value();
        SoundSource source = SoundSource.values()[buffer.readVarInt()];
        float volume = buffer.readFloat();
        float pitch = buffer.readFloat();

        return new SyncableSound(soundEvent, source, volume, pitch);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(BuiltInRegistries.SOUND_EVENT.getId(soundEvent));
        buf.writeVarInt(source.ordinal());
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
    }
}
