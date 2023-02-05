package io.github.davidqf555.minecraft.beams.datagen;

import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;

import java.util.function.Consumer;

public class CustomRecipeProvider extends RecipeProvider {

    public CustomRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        for (DyeColor color : ItemRegistry.COLOR_MODULES.keySet()) {
            ShapelessRecipeBuilder.shapeless(ItemRegistry.COLOR_MODULES.get(color)::get).requires(ItemRegistry.BLANK_MODULE::get).requires(DyeItem.byColor(color)).unlockedBy("has_module", has(ItemRegistry.BLANK_MODULE::get)).save(consumer);
        }
    }
}
