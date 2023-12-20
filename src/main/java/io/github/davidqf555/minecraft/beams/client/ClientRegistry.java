package io.github.davidqf555.minecraft.beams.client;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegistry {

    public static final ModelLayerLocation OMNIDIRECTIONAL_PROJECTOR = new ModelLayerLocation(new ResourceLocation(Beams.ID, "omnidirectional_projector"), "projector");
    public static final ModelLayerLocation OMNIDIRECTIONAL_MIRROR = new ModelLayerLocation(new ResourceLocation(Beams.ID, "omnidirectional_mirror"), "mirror");
    private static final ResourceLocation HOPPER = new ResourceLocation("textures/gui/container/hopper.png");
    private static final ResourceLocation TURRET_MENU = new ResourceLocation(Beams.ID, "textures/gui/container/turret.png");
    private static final ResourceLocation PROJECTOR = new ResourceLocation(Beams.ID, "textures/block/omnidirectional_projector.png");
    private static final ResourceLocation TURRET = new ResourceLocation(Beams.ID, "textures/block/turret.png");

    private ClientRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.BEAM.get(), BeamRenderer<BeamEntity>::new);
        event.registerBlockEntityRenderer(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), context -> new OmnidirectionalProjectorTileEntityRenderer(context, PROJECTOR));
        event.registerBlockEntityRenderer(TileEntityRegistry.OMNIDIRECTIONAL_MIRROR.get(), OmnidirectionalMirrorTileEntityRenderer::new);
        event.registerBlockEntityRenderer(TileEntityRegistry.TURRET.get(), context -> new OmnidirectionalProjectorTileEntityRenderer(context, TURRET));
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OMNIDIRECTIONAL_PROJECTOR, OmnidirectionalProjectorModel::createLayerDefinition);
        event.registerLayerDefinition(OMNIDIRECTIONAL_MIRROR, OmnidirectionalMirrorModel::createLayerDefinition);
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.<ProjectorContainer, SimpleContainerScreen<ProjectorContainer>>register(ContainerRegistry.PROJECTOR.get(), (container, player, name) -> new SimpleContainerScreen<>(HOPPER, container, player, name));
            MenuScreens.<TurretContainer, SimpleContainerScreen<TurretContainer>>register(ContainerRegistry.TURRET.get(), (container, player, name) -> new SimpleContainerScreen<>(TURRET_MENU, container, player, name));
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
