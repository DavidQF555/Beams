package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ContainerRegistry {

    public static final DeferredRegister<MenuType<?>> TYPES = DeferredRegister.create(Registries.MENU, Beams.ID);

    private ContainerRegistry() {
    }

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String name, MenuType.MenuSupplier<T> factory) {
        return TYPES.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }

    public static final DeferredHolder<MenuType<?>, MenuType<ProjectorContainer>> PROJECTOR = register("projector", ProjectorContainer::new);
    public static final DeferredHolder<MenuType<?>, MenuType<TurretContainer>> TURRET = register("turret", TurretContainer::new);


}
