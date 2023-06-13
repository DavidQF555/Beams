package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class TargetingModuleItem extends Item {

    public TargetingModuleItem(Properties properties) {
        super(properties);
    }

    public abstract TargetingModuleType getType(ItemStack stack);

}
