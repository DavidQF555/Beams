package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Beams.ID);

    public static final DeferredHolder<EntityType<?>, EntityType<BeamEntity>> BEAM = register("beam", EntityType.Builder.of(BeamEntity::new, MobCategory.MISC).sized(0, 0));

    private EntityRegistry() {
    }

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return TYPES.register(name, () -> builder.build(name));
    }

}
