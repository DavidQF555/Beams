package io.github.davidqf555.minecraft.beams.client;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegistry {

    private ClientRegistry() {
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BEAM.get(), BeamRenderer<BeamEntity>::new);
        event.enqueueWork(() -> {
            ScreenManager.register(ContainerRegistry.PROJECTOR.get(), ProjectorScreen::new);
        });
    }

    @SubscribeEvent
    public static void onItemColorHandler(ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        for (DyeColor color : ItemRegistry.COLOR_MODULES.keySet()) {
            colors.register(new SimpleItemColor(((stack, layer) -> {
                if (layer == 1) {
                    return color.getColorValue();
                }
                return -1;
            })), ItemRegistry.COLOR_MODULES.get(color)::get);
        }
    }

}
