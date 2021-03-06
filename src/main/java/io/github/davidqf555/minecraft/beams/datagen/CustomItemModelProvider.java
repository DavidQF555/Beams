package io.github.davidqf555.minecraft.beams.datagen;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorModuleItem;
import io.github.davidqf555.minecraft.beams.common.modules.ColorModuleType;
import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class CustomItemModelProvider extends ItemModelProvider {

    public CustomItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Beams.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ResourceLocation color = new ResourceLocation(Beams.ID, "item/color_module");
        for (RegistryObject<ProjectorModuleItem<ColorModuleType>> module : ItemRegistry.COLOR_MODULES.values()) {
            withExistingParent(module.getId().getPath(), color);
        }
    }
}
