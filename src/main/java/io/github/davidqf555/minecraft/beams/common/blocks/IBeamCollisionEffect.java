package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IBeamCollisionEffect {

    default void onBeamCollisionTick(BeamEntity beam, BlockPos pos, BlockState state) {
    }

    default void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
    }

    default void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
    }

}
