package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ContainerRegistry {

    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Beams.ID);

    public static final RegistryObject<MenuType<ProjectorContainer>> PROJECTOR = register("projector", ProjectorContainer::new);
    public static final RegistryObject<MenuType<TurretContainer>> TURRET = register("turret", TurretContainer::new);

    private ContainerRegistry() {
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> factory) {
        return TYPES.register(name, () -> new MenuType<>(factory));
    }

}
