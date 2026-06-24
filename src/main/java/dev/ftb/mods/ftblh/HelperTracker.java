package dev.ftb.mods.ftblh;

import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public enum HelperTracker {
    INSTANCE;

    // maps tracker entity ID -> owning player UUID
    private final Object2IntMap<UUID> knownHelpers = new Object2IntOpenHashMap<>();

    public void unregister(UUID uuid) {
        knownHelpers.removeInt(uuid);
    }

    public void unregister(UUID uuid, LittleHelperEntity helper) {
        if (helper.getId() == knownHelpers.getOrDefault(uuid, 0)) {
            knownHelpers.removeInt(uuid);
        }
    }

    public void register(UUID uuid, int entityId) {
        knownHelpers.put(uuid, entityId);
    }

    public int getHelperId(UUID uuid) {
        return knownHelpers.getOrDefault(uuid, 0);
    }

    public boolean isRegistered(UUID uuid) {
        return knownHelpers.containsKey(uuid);
    }

    public void clear() {
        knownHelpers.clear();
    }

    public Optional<LittleHelperEntity> getActiveHelper(Player player) {
       int id = getHelperId(player.getUUID());
       if (id != 0 && player.level().getEntity(id) instanceof LittleHelperEntity lh) {
           return Optional.of(lh);
       }
       return Optional.empty();
    }

    public boolean validateHelper(Player player, LittleHelperEntity helper) {
        return getActiveHelper(player).map(lh -> lh.getId() == helper.getId()).orElse(false);
    }
}
