package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FireModuleType extends ProjectorModuleType {

    private final int duration;

    public FireModuleType(int duration) {
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target) {
        target.setSecondsOnFire(duration);
    }

    @Override
    public void onBlockTick(BeamEntity beam, BlockPos pos) {
        if (beam.level.isEmptyBlock(pos)) {
            BlockState fire = Blocks.FIRE.defaultBlockState();
            if (((FireBlock) Blocks.FIRE).canSurvive(fire, beam.level, pos)) {
                beam.level.setBlockAndUpdate(pos, fire);
            }
        }
    }
}
