package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.DoubleSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class EntityDataSerializerRegistry {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTRIES = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Beams.ID);

    public static final RegistryObject<DoubleSerializer> DOUBLE = register("double", () -> DoubleSerializer.INSTANCE);

    private EntityDataSerializerRegistry() {
    }

    private static <T extends EntityDataSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer) {
        return ENTRIES.register(name, serializer);
    }

}
