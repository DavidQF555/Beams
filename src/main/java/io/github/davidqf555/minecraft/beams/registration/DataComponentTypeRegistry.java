package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.EntityTypeTargetingModuleItem;
import io.github.davidqf555.minecraft.beams.common.items.PlayerTargetingModuleItem;
import io.github.davidqf555.minecraft.beams.common.items.PointerItem;
import io.github.davidqf555.minecraft.beams.common.items.WhitelistTargetingModuleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public final class DataComponentTypeRegistry {

    public static final DeferredRegister<DataComponentType<?>> TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Beams.ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WHITELIST = register("whitelist", () -> WhitelistTargetingModuleItem.WHITELIST);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Set<UUID>>> MARKED_PLAYERS = register("marked_players", () -> PlayerTargetingModuleItem.MARKED);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Set<ResourceKey<EntityType<?>>>>> MARKED_ENTITIES = register("marked_entities", () -> EntityTypeTargetingModuleItem.MARKED);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Map<UUID, BlockPos>>> CONNECTED = register("connected", () -> PointerItem.CONNECTED_LOC);

    private DataComponentTypeRegistry() {
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, Supplier<DataComponentType<T>> type) {
        return TYPES.register(name, type);
    }

}
