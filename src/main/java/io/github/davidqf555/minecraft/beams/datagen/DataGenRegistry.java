package io.github.davidqf555.minecraft.beams.datagen;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenRegistry {

    private DataGenRegistry() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        if (event.includeClient()) {
            gen.addProvider(true, new CustomItemModelProvider(gen, event.getExistingFileHelper()));
        }
        if (event.includeServer()) {
            gen.addProvider(true, new CustomRecipeProvider(gen));
        }
    }
}
