package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.ColorModuleType;
import io.github.davidqf555.minecraft.beams.common.modules.FireModuleType;
import io.github.davidqf555.minecraft.beams.common.modules.PotionEffectModuleType;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ProjectorModuleRegistry {

    public static final DeferredRegister<ProjectorModuleType> TYPES = DeferredRegister.create(ResourceKey.createRegistryKey(new ResourceLocation(Beams.ID, "module_type")), Beams.ID);
    public static final Map<DyeColor, RegistryObject<ColorModuleType>> COLORS = Arrays.stream(DyeColor.values()).collect(Collectors.toMap(color -> color, color -> register(color.getSerializedName(), () -> new ColorModuleType(color.getFireworkColor()))));
    public static final RegistryObject<PotionEffectModuleType> BRIGHT = register("bright", () -> new PotionEffectModuleType(MobEffects.BLINDNESS, 60, 0));
    public static final RegistryObject<FireModuleType> HOT = register("hot", () -> new FireModuleType(3));
    private static Supplier<IForgeRegistry<ProjectorModuleType>> registry = null;

    private ProjectorModuleRegistry() {
    }

    private static <T extends ProjectorModuleType> RegistryObject<T> register(String name, Supplier<T> type) {
        return TYPES.register(name, type);
    }

    public static IForgeRegistry<ProjectorModuleType> getRegistry() {
        return registry.get();
    }

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        registry = event.create(new RegistryBuilder<ProjectorModuleType>().setName(new ResourceLocation(Beams.ID, "module_type")));
    }

}
