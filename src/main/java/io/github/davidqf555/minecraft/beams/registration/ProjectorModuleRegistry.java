package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ProjectorModuleRegistry {

    public static final DeferredRegister<ProjectorModuleType> TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(Beams.ID, "module_type")), Beams.ID);

    public static final Map<DyeColor, DeferredHolder<ProjectorModuleType, ColorModuleType>> COLORS = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, color -> register(color.getSerializedName(), () -> new ColorModuleType(color.getFireworkColor()))));
    public static final DeferredHolder<ProjectorModuleType, PotionEffectModuleType> BRIGHT = register("bright", () -> new PotionEffectModuleType(MobEffects.BLINDNESS, amt -> amt * 30, amt -> 0));
    public static final DeferredHolder<ProjectorModuleType, FireModuleType> HOT = register("hot", () -> new FireModuleType(amt -> amt * 2));
    public static final DeferredHolder<ProjectorModuleType, DamageModuleType> DAMAGE = register("damage", () -> new DamageModuleType(10, amt -> (float) amt));
    public static final DeferredHolder<ProjectorModuleType, EnderModuleType> ENDER = register("ender", () -> new EnderModuleType(16));
    public static final DeferredHolder<ProjectorModuleType, ForceModuleType> FORCE = register("force", () -> new ForceModuleType(amt -> amt * 0.05));
    public static final DeferredHolder<ProjectorModuleType, MiningModuleType> MINING = register("mining", () -> new MiningModuleType(20, amt -> amt * 2f));
    public static final DeferredHolder<ProjectorModuleType, LayersModuleType> LAYERS = register("layers", () -> new LayersModuleType(amt -> amt));
    public static final DeferredHolder<ProjectorModuleType, SizeModuleType> GROWTH = register("growth", () -> new SizeModuleType(amt -> 1.0, amt -> amt * 0.025));
    public static final DeferredHolder<ProjectorModuleType, SizeModuleType> SHRINK = register("shrink", () -> new SizeModuleType(amt -> Math.pow(0.75, amt), amt -> 0.0));
    public static final DeferredHolder<ProjectorModuleType, ForceModuleType> TRACTOR = register("tractor", () -> new ForceModuleType(amt -> amt * -0.025));
    public static final DeferredHolder<ProjectorModuleType, FreezeModuleType> FREEZE = register("freeze", () -> new FreezeModuleType(amt -> amt, amt -> amt * 20, amt -> amt));

    private static Registry<ProjectorModuleType> registry = null;

    private ProjectorModuleRegistry() {
    }

    private static <T extends ProjectorModuleType> DeferredHolder<ProjectorModuleType, T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static Registry<ProjectorModuleType> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<ProjectorModuleType>(ResourceKey.createRegistryKey(new ResourceLocation(Beams.ID, "module_type"))));
    }

}
