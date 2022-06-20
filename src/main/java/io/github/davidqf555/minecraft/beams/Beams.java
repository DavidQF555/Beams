package io.github.davidqf555.minecraft.beams;

import io.github.davidqf555.minecraft.beams.registration.BlockRegistry;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("beams")
public class Beams {

    public static final String ID = "beams";

    public Beams() {
        addRegistries(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addRegistries(IEventBus bus) {
        BlockRegistry.BLOCKS.register(bus);
        TileEntityRegistry.TYPES.register(bus);
        EntityRegistry.TYPES.register(bus);
        ItemRegistry.ITEMS.register(bus);
    }

}
