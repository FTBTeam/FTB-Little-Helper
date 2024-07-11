package dev.ftb.mods.ftblh.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.ftb.mods.ftblh.FTBLittleHelper;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(FTBLittleHelper.MOD_ID, Registries.ENTITY_TYPE);

    public static final Supplier<EntityType<LittleHelperEntity>> LITTLE_HELPER = register("little_helper", ModEntityTypes::littleHelper);

    private static <E extends Entity> Supplier<EntityType<E>> register(final String name, final Supplier<EntityType.Builder<E>> sup) {
        return ENTITY_TYPES.register(name, () -> sup.get().build(name));
    }

    public static void register() {
        ENTITY_TYPES.register();
    }

    private static EntityType.Builder<LittleHelperEntity> littleHelper() {
        return EntityType.Builder.of(LittleHelperEntity::new, MobCategory.MISC)
                .sized(0.35f, 0.6f)
                .clientTrackingRange(8)
                .noSave()
                .fireImmune()
                .updateInterval(2);
    }
}
