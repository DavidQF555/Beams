package io.github.davidqf555.minecraft.beams.datagen;

import io.github.davidqf555.minecraft.beams.registration.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;

import java.util.concurrent.CompletableFuture;

public class CustomRecipeProvider extends RecipeProvider {

    public CustomRecipeProvider(PackOutput generatorIn, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generatorIn, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        for (DyeColor color : ItemRegistry.COLOR_MODULES.keySet()) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemRegistry.COLOR_MODULES.get(color)::get).requires(ItemRegistry.BLANK_MODULE::get).requires(DyeItem.byColor(color)).unlockedBy("has_module", has(ItemRegistry.BLANK_MODULE::get)).save(consumer);
        }
    }
}
