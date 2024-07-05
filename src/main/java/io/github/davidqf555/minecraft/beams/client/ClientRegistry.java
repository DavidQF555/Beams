package io.github.davidqf555.minecraft.beams.client;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Beams.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegistry {

    public static final ModelLayerLocation OMNIDIRECTIONAL_PROJECTOR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Beams.ID, "omnidirectional_projector"), "projector");
    public static final ModelLayerLocation OMNIDIRECTIONAL_MIRROR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Beams.ID, "omnidirectional_mirror"), "mirror");
    private static final ResourceLocation HOPPER = ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/gui/container/hopper.png");
    private static final ResourceLocation TURRET_MENU = ResourceLocation.fromNamespaceAndPath(Beams.ID, "textures/gui/container/turret.png");
    private static final ResourceLocation PROJECTOR = ResourceLocation.fromNamespaceAndPath(Beams.ID, "textures/block/omnidirectional_projector.png");
    private static final ResourceLocation TURRET = ResourceLocation.fromNamespaceAndPath(Beams.ID, "textures/block/turret.png");

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
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.<ProjectorContainer, SimpleContainerScreen<ProjectorContainer>>register(ContainerRegistry.PROJECTOR.get(), (container, player, name) -> new SimpleContainerScreen<>(HOPPER, container, player, name));
        event.<TurretContainer, SimpleContainerScreen<TurretContainer>>register(ContainerRegistry.TURRET.get(), (container, player, name) -> new SimpleContainerScreen<>(TURRET_MENU, container, player, name));
    }

    @SubscribeEvent
    public static void onItemColorHandler(RegisterColorHandlersEvent.Item event) {
        for (DyeColor dye : ItemRegistry.COLOR_MODULES.keySet()) {
            int color = dye.getTextureDiffuseColor();
            event.register(new SimpleItemColor(((stack, layer) -> {
                if (layer == 1) {
                    return color;
                }
                return -1;
            })), ItemRegistry.COLOR_MODULES.get(dye)::get);
        }
    }

}
