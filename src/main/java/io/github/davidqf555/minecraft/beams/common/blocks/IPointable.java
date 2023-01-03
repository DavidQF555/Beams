package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IPointable {

    @Nullable
    UUID getConnectionID(World world, BlockPos pos);

    void onPoint(World world, BlockPos pos, Vector3d target);

}
