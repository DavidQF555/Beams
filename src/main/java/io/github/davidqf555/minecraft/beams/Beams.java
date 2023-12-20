package io.github.davidqf555.minecraft.beams;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.registration.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("beams")
public class Beams {

    public static final String ID = "beams";

    public Beams() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
        addRegistries(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        EntityRegistry.TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        ProjectorModuleRegistry.TYPES.register(bus);
        ContainerRegistry.TYPES.register(bus);
        CreativeModeTabRegistry.TABS.register(bus);
        EntityDataSerializerRegistry.ENTRIES.register(bus);
    }

}
