package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IPointable {

    @Nullable
    UUID getConnectionID(Level world, BlockPos pos);

    void onPoint(Level world, BlockPos pos, Vec3 target);

}
