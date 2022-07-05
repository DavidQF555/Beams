package io.github.davidqf555.minecraft.beams.common.modules;

import java.util.function.Function;

public class TotalSizeModuleType extends ProjectorModuleType {

    private final Function<Integer, Double> start, end;

    public TotalSizeModuleType(Function<Integer, Double> start, Function<Integer, Double> end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public double getStartSizeFactor(int amt) {
        return start.apply(amt);
    }

    @Override
    public double getEndSizeFactor(int amt) {
        return end.apply(amt);
    }

}
