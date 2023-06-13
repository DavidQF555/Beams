package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TargetingModuleItem extends Item {

    private final TargetingModuleType type;

    public TargetingModuleItem(TargetingModuleType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public TargetingModuleType getType(ItemStack stack) {
        return type;
    }

}
