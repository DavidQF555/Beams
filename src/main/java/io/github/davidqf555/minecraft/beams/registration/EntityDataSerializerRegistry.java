package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class EntityDataSerializerRegistry {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTRIES = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Beams.ID);

    public static final EntityDataSerializer<Double> DOUBLE = EntityDataSerializer.forValueType(ByteBufCodecs.DOUBLE);

    static {
        register("double", () -> DOUBLE);
    }

    private EntityDataSerializerRegistry() {
    }

    private static <T extends EntityDataSerializer<?>> DeferredHolder<EntityDataSerializer<?>, T> register(String name, Supplier<T> serializer) {
        return ENTRIES.register(name, serializer);
    }

}
