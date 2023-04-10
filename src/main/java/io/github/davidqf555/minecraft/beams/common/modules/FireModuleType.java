package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

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
