package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class EntityRegistry {

    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Beams.ID);

    public static final RegistryObject<EntityType<BeamEntity>> BEAM = register("beam", EntityType.Builder.of(BeamEntity::new, EntityClassification.MISC).sized(0, 0));

    private EntityRegistry() {
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return TYPES.register(name, () -> builder.build(name));
    }

}
