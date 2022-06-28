package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.item.Item;

import java.util.function.Supplier;

public class ProjectorModuleItem<T extends ProjectorModuleType> extends Item {

    private final Supplier<T> type;

    public ProjectorModuleItem(Supplier<T> type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public T getType() {
        return type.get();
    }

}
