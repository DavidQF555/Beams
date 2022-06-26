package io.github.davidqf555.minecraft.beams.client;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Beams.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RendererRegistry {

    private RendererRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.BEAM.get(), BeamRenderer<BeamEntity>::new);
    }

}
