package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public class FireModuleType extends ProjectorModuleType {

    private final Function<Integer, Integer> duration;

    public FireModuleType(Function<Integer, Integer> duration) {
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        target.setSecondsOnFire(duration.apply(amt));
    }

    @Override
    public void onBlockTick(BeamEntity beam, BlockPos pos, int amt) {
        if (beam.level.isEmptyBlock(pos)) {
            BlockState fire = Blocks.FIRE.defaultBlockState();
            if (((FireBlock) Blocks.FIRE).canSurvive(fire, beam.level, pos)) {
                beam.level.setBlockAndUpdate(pos, fire);
            }
        }
    }

    @Override
    public boolean shouldTickEntities() {
        return true;
    }

    @Override
    public boolean shouldTickBlocks() {
        return true;
    }
}
