package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.world.item.Item;

public class TargetingModuleItem extends Item {

    private final TargetingModuleType type;

    public TargetingModuleItem(TargetingModuleType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public TargetingModuleType getType() {
        return type;
    }

}
