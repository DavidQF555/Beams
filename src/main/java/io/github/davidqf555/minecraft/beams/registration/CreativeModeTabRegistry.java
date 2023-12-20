package io.github.davidqf555.minecraft.beams.registration;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CreativeModeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Beams.ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = register("main", CreativeModeTab.builder().icon(() -> ItemRegistry.PROJECTOR.get().getDefaultInstance()).title(Component.translatable(Util.makeDescriptionId("itemGroup", new ResourceLocation(Beams.ID, "main")))));

    private CreativeModeTabRegistry() {
    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> register(String name, CreativeModeTab.Builder builder) {
        return TABS.register(name, builder::build);
    }

}
