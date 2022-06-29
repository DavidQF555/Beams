package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.PortableProjectorItem;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorModuleItem;
import io.github.davidqf555.minecraft.beams.common.modules.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Beams.ID);

    public static final RegistryObject<BlockItem> PROJECTOR = register("projector", () -> new BlockItem(BlockRegistry.PROJECTOR.get(), new Item.Properties().tab(Beams.GROUP)));
    public static final RegistryObject<BlockItem> TILTED_PROJECTOR = register("tilted_projector", () -> new BlockItem(BlockRegistry.TILTED_PROJECTOR.get(), new Item.Properties().tab(Beams.GROUP)));
    public static final RegistryObject<PortableProjectorItem> PORTABLE_PROJECTOR = register("portable_projector", () -> new PortableProjectorItem(new Item.Properties().stacksTo(1).tab(Beams.GROUP)));

    public static final RegistryObject<Item> BLANK_MODULE = register("blank_module", () -> new Item(new Item.Properties().tab(Beams.GROUP)));
    public static final Map<DyeColor, RegistryObject<ProjectorModuleItem<ColorModuleType>>> COLOR_MODULES = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, color -> register(color.getSerializedName() + "_module", () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.COLORS.get(color), new Item.Properties().tab(Beams.GROUP)))));
    public static final RegistryObject<ProjectorModuleItem<PotionEffectModuleType>> BRIGHT_MODULE = register("bright_module", () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.BRIGHT, new Item.Properties().tab(Beams.GROUP)));
    public static final RegistryObject<ProjectorModuleItem<FireModuleType>> HOT_MODULE = register("hot_module", () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.HOT, new Item.Properties().tab(Beams.GROUP)));
    public static final RegistryObject<ProjectorModuleItem<DamageModuleType>> DAMAGE_MODULE = register("damage_module", () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.DAMAGE, new Item.Properties().tab(Beams.GROUP)));
    public static final RegistryObject<ProjectorModuleItem<EnderModuleType>> ENDER_MODULE = register("ender_module", () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.ENDER, new Item.Properties().tab(Beams.GROUP)));
    public static final RegistryObject<ProjectorModuleItem<ForceModuleType>> FORCE_MODULE = register("force_module", () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.FORCE, new Item.Properties().tab(Beams.GROUP)));

    private ItemRegistry() {
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

}
