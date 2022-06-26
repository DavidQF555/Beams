package io.github.davidqf555.minecraft.beams;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.registration.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("beams")
public class Beams {

    public static final String ID = "beams";
    public static final ItemGroup GROUP = new ItemGroup(ID) {
        @Override
        public ItemStack makeIcon() {
            return ItemRegistry.PROJECTOR.get().getDefaultInstance();
        }
    };

    public Beams() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
        addRegistries(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        EntityRegistry.TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
        ProjectorModuleRegistry.TYPES.register(bus);
        ContainerRegistry.TYPES.register(bus);
    }

}
