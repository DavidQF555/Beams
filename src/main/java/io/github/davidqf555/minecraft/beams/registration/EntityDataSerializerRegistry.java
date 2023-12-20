package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.DoubleSerializer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class EntityDataSerializerRegistry {

    public static final DeferredRegister<DataSerializerEntry> ENTRIES = DeferredRegister.create(ForgeRegistries.DATA_SERIALIZERS, Beams.ID);

    public static final RegistryObject<DataSerializerEntry> DOUBLE = register("double", () -> DoubleSerializer.INSTANCE);

    private EntityDataSerializerRegistry() {
    }

    private static RegistryObject<DataSerializerEntry> register(String name, Supplier<IDataSerializer<?>> serializer) {
        return ENTRIES.register(name, () -> new DataSerializerEntry(serializer.get()));
    }

}
