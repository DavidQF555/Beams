package io.github.davidqf555.minecraft.beams.client;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class SimpleItemColor implements ItemColor {

    private final BiFunction<ItemStack, Integer, Integer> function;

    public SimpleItemColor(BiFunction<ItemStack, Integer, Integer> function) {
        this.function = function;
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        return function.apply(stack, layer);
    }
}
