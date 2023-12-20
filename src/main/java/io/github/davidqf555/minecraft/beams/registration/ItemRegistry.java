package io.github.davidqf555.minecraft.beams.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.*;
import io.github.davidqf555.minecraft.beams.common.modules.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Beams.ID);
    private static final List<Pair<ResourceKey<CreativeModeTab>, Supplier<? extends Item>>> TABS = new ArrayList<>();

    public static final DeferredHolder<Item, BlockItem> PROJECTOR = register("projector", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.PROJECTOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TILTED_PROJECTOR = register("tilted_projector", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.TILTED_PROJECTOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> OMNIDIRECTIONAL_PROJECTOR = register("omnidirectional_projector", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> MIRROR = register("mirror", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.MIRROR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> PHOTODETECTOR = register("photodetector", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.PHOTODETECTOR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> OMNIDIRECTIONAL_MIRROR = register("omnidirectional_mirror", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.OMNIDIRECTIONAL_MIRROR.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TURRET = register("turret", CreativeModeTabRegistry.MAIN.getKey(), () -> new BlockItem(BlockRegistry.TURRET.get(), new Item.Properties()));

    public static final DeferredHolder<Item, PortableProjectorItem> PORTABLE_PROJECTOR = register("portable_projector", CreativeModeTabRegistry.MAIN.getKey(), () -> new PortableProjectorItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, PointerItem> POINTER = register("projector_pointer", CreativeModeTabRegistry.MAIN.getKey(), () -> new PointerItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> BLANK_MODULE = register("blank_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new Item(new Item.Properties()));
    public static final Map<DyeColor, DeferredHolder<Item, ProjectorModuleItem<ColorModuleType>>> COLOR_MODULES = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, color -> register(color.getSerializedName() + "_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.COLORS.get(color), new Item.Properties()))));
    public static final DeferredHolder<Item, ProjectorModuleItem<PotionEffectModuleType>> BRIGHT_MODULE = register("bright_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.BRIGHT, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<FireModuleType>> HOT_MODULE = register("hot_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.HOT, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<DamageModuleType>> DAMAGE_MODULE = register("damage_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.DAMAGE, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<EnderModuleType>> ENDER_MODULE = register("ender_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.ENDER, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<ForceModuleType>> FORCE_MODULE = register("force_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.FORCE, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<MiningModuleType>> MINING_MODULE = register("mining_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.MINING, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<LayersModuleType>> LAYERS_MODULE = register("layers_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.LAYERS, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<SizeModuleType>> GROWTH_MODULE = register("growth_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.GROWTH, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<SizeModuleType>> SHRINK_MODULE = register("shrink_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.SHRINK, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<ForceModuleType>> TRACTOR_MODULE = register("tractor_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.TRACTOR, new Item.Properties()));
    public static final DeferredHolder<Item, ProjectorModuleItem<FreezeModuleType>> FREEZE_MODULE = register("freeze_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.FREEZE, new Item.Properties()));

    public static final DeferredHolder<Item, PlayerTargetingModuleItem> PLAYER_TARGETING_MODULE = register("player_targeting_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new PlayerTargetingModuleItem(new Item.Properties()));
    public static final DeferredHolder<Item, EntityTypeTargetingModuleItem> ENTITY_TYPE_TARGETING_MODULE = register("entity_type_targeting_module", CreativeModeTabRegistry.MAIN.getKey(), () -> new EntityTypeTargetingModuleItem(new Item.Properties()));

    private ItemRegistry() {
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        TABS.stream().filter(pair -> event.getTabKey().equals(pair.getFirst())).map(Pair::getSecond).map(Supplier::get).map(Item::getDefaultInstance).forEach(stack -> event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }

    private static <T extends Item> DeferredHolder<Item, T> register(String name, ResourceKey<CreativeModeTab> tab, Supplier<T> item) {
        DeferredHolder<Item, T> out = ITEMS.register(name, item);
        if (tab != null) {
            TABS.add(Pair.of(tab, out));
        }
        return out;
    }

}
