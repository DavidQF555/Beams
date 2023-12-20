package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.DoubleSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class EntityDataSerializerRegistry {

    public static final DeferredRegister<DataSerializerEntry> ENTRIES = DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, Beams.ID);

    public static final RegistryObject<DataSerializerEntry> DOUBLE = register("double", () -> DoubleSerializer.INSTANCE);

    private EntityDataSerializerRegistry() {
    }

    private static RegistryObject<DataSerializerEntry> register(String name, Supplier<EntityDataSerializer<?>> serializer) {
        return ENTRIES.register(name, () -> new DataSerializerEntry(serializer.get()));
    }

}
