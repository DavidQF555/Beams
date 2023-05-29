package io.github.davidqf555.minecraft.beams.client;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.TurretContainer;
import io.github.davidqf555.minecraft.beams.registration.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientRegistry {

    private static final ResourceLocation HOPPER = new ResourceLocation("textures/gui/container/hopper.png");
    private static final ResourceLocation TURRET_MENU = new ResourceLocation(Beams.ID, "textures/gui/container/turret.png");
    private static final ResourceLocation PROJECTOR = new ResourceLocation(Beams.ID, "textures/block/omnidirectional_projector.png");
    private static final ResourceLocation TURRET = new ResourceLocation(Beams.ID, "textures/block/turret.png");

    private ClientRegistry() {
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.BEAM.get(), BeamRenderer<BeamEntity>::new);
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntityRenderer(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), context -> new OmnidirectionalProjectorTileEntityRenderer(context, PROJECTOR));
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntityRenderer(TileEntityRegistry.TURRET.get(), context -> new OmnidirectionalProjectorTileEntityRenderer(context, TURRET));
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntityRenderer(TileEntityRegistry.OMNIDIRECTIONAL_MIRROR.get(), OmnidirectionalMirrorTileEntityRenderer::new);
        RenderTypeLookup.setRenderLayer(BlockRegistry.OMNIDIRECTIONAL_PROJECTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.OMNIDIRECTIONAL_MIRROR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockRegistry.TURRET.get(), RenderType.cutout());
        event.enqueueWork(() -> {
            ScreenManager.<ProjectorContainer, SimpleContainerScreen<ProjectorContainer>>register(ContainerRegistry.PROJECTOR.get(), (container, player, name) -> new SimpleContainerScreen<>(HOPPER, container, player, name));
            ScreenManager.<TurretContainer, SimpleContainerScreen<TurretContainer>>register(ContainerRegistry.TURRET.get(), (container, player, name) -> new SimpleContainerScreen<>(TURRET_MENU, container, player, name));
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
