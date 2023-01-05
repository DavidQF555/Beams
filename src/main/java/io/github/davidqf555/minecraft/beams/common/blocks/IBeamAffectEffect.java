package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IBeamAffectEffect {

    default void onBeamAffectTick(BeamEntity beam, BlockPos pos, BlockState state) {
    }

    default void onBeamStartAffect(BeamEntity beam, BlockPos pos, BlockState state) {
    }

    default void onBeamStopAffect(BeamEntity beam, BlockPos pos, BlockState state) {
    }

}
