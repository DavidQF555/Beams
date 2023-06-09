package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;

import java.util.function.Function;

public class MiningModuleType extends ProjectorModuleType {

    private final int period;
    private final Function<Integer, Float> strength;

    public MiningModuleType(int period, Function<Integer, Float> strength) {
        this.period = period;
        this.strength = strength;
    }

    @Override
    public void onCollisionTick(BeamEntity beam, BlockPos pos, int amt) {
        if (beam.level().getGameTime() % period == 0) {
            float speed = beam.level().getBlockState(pos).getDestroySpeed(beam.level(), pos);
            if (speed != -1 && speed <= strength.apply(amt)) {
                beam.level().destroyBlock(pos, true);
            }
        }
    }

    @Override
    public boolean shouldTickBlocks() {
        return true;
    }

}
