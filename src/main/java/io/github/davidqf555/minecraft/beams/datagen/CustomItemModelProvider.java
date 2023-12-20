package io.github.davidqf555.minecraft.beams.datagen;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorModuleItem;
import io.github.davidqf555.minecraft.beams.common.modules.ColorModuleType;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CustomItemModelProvider extends ItemModelProvider {

    public CustomItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Beams.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation color = new ResourceLocation(Beams.ID, "item/color_module");
        for (DeferredHolder<Item, ProjectorModuleItem<ColorModuleType>> module : ItemRegistry.COLOR_MODULES.values()) {
            withExistingParent(module.getId().getPath(), color);
        }
    }
}
