package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Beams.ID);

    public static final RegistryObject<BlockItem> PROJECTOR = register("projector", () -> new BlockItem(BlockRegistry.PROJECTOR.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<BlockItem> TILTED_PROJECTOR = register("tilted_projector", () -> new BlockItem(BlockRegistry.TILTED_PROJECTOR.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

}
