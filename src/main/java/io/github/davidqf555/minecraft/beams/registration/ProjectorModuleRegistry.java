package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.*;
import net.minecraft.item.DyeColor;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ProjectorModuleRegistry {

    public static final DeferredRegister<ProjectorModuleType> TYPES = DeferredRegister.create(ProjectorModuleType.class, Beams.ID);
    public static final Map<DyeColor, RegistryObject<ColorModuleType>> COLORS = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, color -> register(color.getSerializedName(), () -> new ColorModuleType(color.getFireworkColor()))));
    public static final RegistryObject<PotionEffectModuleType> BRIGHT = register("bright", () -> new PotionEffectModuleType(Effects.BLINDNESS, amt -> amt * 30, amt -> 0));
    public static final RegistryObject<FireModuleType> HOT = register("hot", () -> new FireModuleType(amt -> amt * 2));
    public static final RegistryObject<DamageModuleType> DAMAGE = register("damage", () -> new DamageModuleType(10, amt -> (float) amt));
    public static final RegistryObject<EnderModuleType> ENDER = register("ender", () -> new EnderModuleType(16));
    public static final RegistryObject<ForceModuleType> FORCE = register("force", () -> new ForceModuleType(amt -> amt * 0.05));
    public static final RegistryObject<MiningModuleType> MINING = register("mining", () -> new MiningModuleType(20, amt -> amt * 2f));
    public static final RegistryObject<LayersModuleType> LAYERS = register("layers", () -> new LayersModuleType(amt -> amt));
    public static final RegistryObject<SizeModuleType> GROWTH = register("growth", () -> new SizeModuleType(amt -> 1.0, amt -> amt * 2 + 1.0));
    public static final RegistryObject<SizeModuleType> SHRINK = register("shrink", () -> new SizeModuleType(amt -> Math.pow(0.75, amt), amt -> Math.pow(0.75, amt)));
    public static final RegistryObject<ForceModuleType> TRACTOR = register("tractor", () -> new ForceModuleType(amt -> amt * -0.025));
    public static final RegistryObject<FreezeModuleType> FREEZE = register("freeze", () -> new FreezeModuleType(amt -> amt, amt -> amt * 20));

    private static IForgeRegistry<ProjectorModuleType> registry = null;

    private ProjectorModuleRegistry() {
    }

    private static <T extends ProjectorModuleType> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static IForgeRegistry<ProjectorModuleType> getRegistry() {
        return registry;
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        registry = new RegistryBuilder<ProjectorModuleType>().setType(ProjectorModuleType.class).setName(new ResourceLocation(Beams.ID, "module_type")).create();
    }

}
