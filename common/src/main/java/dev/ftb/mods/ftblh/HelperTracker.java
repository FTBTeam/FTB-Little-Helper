package dev.ftb.mods.ftblh;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.UUID;

public enum HelperTracker {
    INSTANCE;

    // maps tracker entity ID -> owning player UUID
    private final Object2IntMap<UUID> knownHelpers = new Object2IntOpenHashMap<>();

    public void unregister(UUID uuid) {
        knownHelpers.removeInt(uuid);
    }

    public void register(UUID uuid, int entityId) {
        knownHelpers.put(uuid, entityId);
    }

    public int getHelperId(UUID uuid) {
        return knownHelpers.getOrDefault(uuid, 0);
    }
}
