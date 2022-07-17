package io.github.davidqf555.minecraft.beams.client;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegistry {

    public static final ModelLayerLocation OMNIDIRECTIONAL_PROJECTOR = new ModelLayerLocation(new ResourceLocation(Beams.ID, "omnidirectional_projector"), "projector");

    private ClientRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.BEAM.get(), BeamRenderer<BeamEntity>::new);
        event.registerBlockEntityRenderer(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), OmnidirectionalProjectorTileEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OMNIDIRECTIONAL_PROJECTOR, OmnidirectionalProjectorModel::createLayerDefinition);
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ContainerRegistry.PROJECTOR.get(), ProjectorScreen::new);
        });
    }

    @SubscribeEvent
    public static void onItemColorHandler(RegisterColorHandlersEvent.Item event) {
        ItemColors colors = event.getItemColors();
        for (DyeColor dye : ItemRegistry.COLOR_MODULES.keySet()) {
            float[] diffuse = dye.getTextureDiffuseColors();
            int color = FastColor.ARGB32.color(0xFF, (int) (diffuse[0] * 255), (int) (diffuse[1] * 255), (int) (diffuse[2] * 255));
            colors.register(new SimpleItemColor(((stack, layer) -> {
                if (layer == 1) {
                    return color;
                }
                return -1;
            })), ItemRegistry.COLOR_MODULES.get(dye)::get);
        }
    }

}
