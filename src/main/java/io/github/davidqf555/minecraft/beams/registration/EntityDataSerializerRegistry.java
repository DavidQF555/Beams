package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.DoubleSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class EntityDataSerializerRegistry {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTRIES = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Beams.ID);

    public static final DeferredHolder<EntityDataSerializer<?>, DoubleSerializer> DOUBLE = register("double", () -> DoubleSerializer.INSTANCE);

    private EntityDataSerializerRegistry() {
    }

    private static <T extends EntityDataSerializer<?>> DeferredHolder<EntityDataSerializer<?>, T> register(String name, Supplier<T> serializer) {
        return ENTRIES.register(name, serializer);
    }

}
