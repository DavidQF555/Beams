package io.github.davidqf555.minecraft.beams.common.modules;

import java.util.function.Function;

public class EndSizeModuleType extends ProjectorModuleType {

    private final Function<Integer, Double> size;

    public EndSizeModuleType(Function<Integer, Double> size) {
        this.size = size;
    }

    @Override
    public double getEndSizeFactor(int amt) {
        return size.apply(amt);
    }
}
