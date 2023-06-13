package io.github.davidqf555.minecraft.beams.registration;

import com.mojang.datafixers.util.Pair;
import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.*;
import io.github.davidqf555.minecraft.beams.common.modules.*;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Beams.ID);
    private static final List<Pair<ResourceKey<CreativeModeTab>, Supplier<? extends Item>>> TABS = new ArrayList<>();
    private static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(Beams.ID, "main"));

    public static final RegistryObject<BlockItem> PROJECTOR = register("projector", getTab(), () -> new BlockItem(BlockRegistry.PROJECTOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> TILTED_PROJECTOR = register("tilted_projector", getTab(), () -> new BlockItem(BlockRegistry.TILTED_PROJECTOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> OMNIDIRECTIONAL_PROJECTOR = register("omnidirectional_projector", getTab(), () -> new BlockItem(BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> MIRROR = register("mirror", getTab(), () -> new BlockItem(BlockRegistry.MIRROR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> PHOTODETECTOR = register("photodetector", getTab(), () -> new BlockItem(BlockRegistry.PHOTODETECTOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> OMNIDIRECTIONAL_MIRROR = register("omnidirectional_mirror", getTab(), () -> new BlockItem(BlockRegistry.OMNIDIRECTIONAL_MIRROR.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> TURRET = register("turret", getTab(), () -> new BlockItem(BlockRegistry.TURRET.get(), new Item.Properties()));

    public static final RegistryObject<PortableProjectorItem> PORTABLE_PROJECTOR = register("portable_projector", getTab(), () -> new PortableProjectorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<PointerItem> POINTER = register("projector_pointer", getTab(), () -> new PointerItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> BLANK_MODULE = register("blank_module", getTab(), () -> new Item(new Item.Properties()));
    public static final Map<DyeColor, RegistryObject<ProjectorModuleItem<ColorModuleType>>> COLOR_MODULES = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, color -> register(color.getSerializedName() + "_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.COLORS.get(color), new Item.Properties()))));
    public static final RegistryObject<ProjectorModuleItem<PotionEffectModuleType>> BRIGHT_MODULE = register("bright_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.BRIGHT, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<FireModuleType>> HOT_MODULE = register("hot_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.HOT, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<DamageModuleType>> DAMAGE_MODULE = register("damage_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.DAMAGE, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<EnderModuleType>> ENDER_MODULE = register("ender_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.ENDER, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<ForceModuleType>> FORCE_MODULE = register("force_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.FORCE, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<MiningModuleType>> MINING_MODULE = register("mining_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.MINING, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<LayersModuleType>> LAYERS_MODULE = register("layers_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.LAYERS, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<SizeModuleType>> GROWTH_MODULE = register("growth_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.GROWTH, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<SizeModuleType>> SHRINK_MODULE = register("shrink_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.SHRINK, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<ForceModuleType>> TRACTOR_MODULE = register("tractor_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.TRACTOR, new Item.Properties()));
    public static final RegistryObject<ProjectorModuleItem<FreezeModuleType>> FREEZE_MODULE = register("freeze_module", getTab(), () -> new ProjectorModuleItem<>(ProjectorModuleRegistry.FREEZE, new Item.Properties()));

    public static final RegistryObject<PlayerTargetingModuleItem> PLAYER_TARGETING_MODULE = register("player_targeting_module", getTab(), () -> new PlayerTargetingModuleItem(new Item.Properties()));
    public static final RegistryObject<EntityTypeTargetingModuleItem> ENTITY_TYPE_TARGETING_MODULE = register("entity_type_targeting_module", getTab(), () -> new EntityTypeTargetingModuleItem(new Item.Properties()));

    private ItemRegistry() {
    }

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB.location(), CreativeModeTab.builder().icon(() -> PROJECTOR.get().getDefaultInstance()).title(Component.translatable(Util.makeDescriptionId("itemGroup", TAB.location()))).build());
        });
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        TABS.stream().filter(pair -> event.getTabKey().equals(pair.getFirst())).map(Pair::getSecond).forEach(event::accept);
    }

    public static ResourceKey<CreativeModeTab> getTab() {
        return TAB;
    }

    private static <T extends Item> RegistryObject<T> register(String name, ResourceKey<CreativeModeTab> tab, Supplier<T> item) {
        RegistryObject<T> out = ITEMS.register(name, item);
        if (tab != null) {
            TABS.add(Pair.of(tab, out));
        }
        return out;
    }

}
