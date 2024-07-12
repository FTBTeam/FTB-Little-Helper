package dev.ftb.mods.ftblh;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class PersistedHelpers extends SavedData {
    private static final String DATA_NAME = "ftblh-persisted";

    // tracks UUIDs of players who have the helper in stays-up mode, so it can be summoned on server started
    private final Set<UUID> persistedPlayerUUIDs = new HashSet<>();

    private PersistedHelpers() {
    }

    private static PersistedHelpers load(CompoundTag tag) {
        return new PersistedHelpers().readNBT(tag);
    }

    public static PersistedHelpers getInstance(MinecraftServer server) {
        ServerLevel overworld = Objects.requireNonNull(server.getLevel(Level.OVERWORLD));
        return overworld.getDataStorage().computeIfAbsent(PersistedHelpers::load, PersistedHelpers::new, DATA_NAME);
    }

    public void add(Player player) {
        // called when player shows the helper
        if (persistedPlayerUUIDs.add(player.getUUID())) {
            setDirty();
        }
    }

    public boolean checkAndRemove(Player player) {
        // called when player hides the helper, and when player logs in
        if (persistedPlayerUUIDs.remove(player.getUUID())) {
            setDirty();
            return true;
        }
        return false;
    }

    private PersistedHelpers readNBT(CompoundTag tag) {
        persistedPlayerUUIDs.clear();

        ListTag list = tag.getList("uuids", Tag.TAG_STRING);
        list.forEach(el -> {
            try {
                UUID id = UUID.fromString(el.getAsString());
                persistedPlayerUUIDs.add(id);
            } catch (IllegalArgumentException e) {
                FTBLittleHelper.LOGGER.error("invalid UUID {} in persisted helpers saved data", el.getAsString());
            }
        });

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("uuids", listOfStr(persistedPlayerUUIDs.stream().map(UUID::toString).toList()));
        return compoundTag;
    }

    private static ListTag listOfStr(List<String> l) {
        return Util.make(new ListTag(), t -> l.forEach(s -> t.add(StringTag.valueOf(s))));
    }
}
