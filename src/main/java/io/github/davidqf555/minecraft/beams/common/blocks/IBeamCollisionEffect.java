package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface IBeamCollisionEffect {

    default void onBeamCollisionTick(BeamEntity beam, BlockPos pos, BlockState state) {
    }

    default void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
    }

    default void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
    }

}
