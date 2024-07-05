package io.github.davidqf555.minecraft.beams.common.events;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorInventory;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Beams.ID, bus = EventBusSubscriber.Bus.MOD)
public final class CapabilityRegistry {

    private CapabilityRegistry() {
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(ProjectorInventory.CAPABILITY, (stack, context) -> new ProjectorInventory(), ItemRegistry.PORTABLE_PROJECTOR.get());
    }

}
