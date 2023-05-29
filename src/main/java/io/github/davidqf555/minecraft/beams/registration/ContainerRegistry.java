package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ContainerRegistry {

    public static final DeferredRegister<ContainerType<?>> TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Beams.ID);

    private ContainerRegistry() {
    }

    private static <T extends Container> RegistryObject<ContainerType<T>> register(String name, ContainerType.IFactory<T> factory) {
        return TYPES.register(name, () -> new ContainerType<>(factory));
    }

    public static final RegistryObject<ContainerType<ProjectorContainer>> PROJECTOR = register("projector", ProjectorContainer::new);
    public static final RegistryObject<ContainerType<TurretContainer>> TURRET = register("turret", TurretContainer::new);

}
