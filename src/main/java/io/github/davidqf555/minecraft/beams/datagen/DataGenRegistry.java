package io.github.davidqf555.minecraft.beams.datagen;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            gen.addProvider(true, new CustomItemModelProvider(gen.getPackOutput(), event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            gen.addProvider(true, new CustomRecipeProvider(gen.getPackOutput(), event.getLookupProvider()));
        }
    }
}
