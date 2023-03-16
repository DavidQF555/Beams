package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMirrorBlock extends AbstractRedirectorBlock {

    protected static final double OFFSET = 0.2;

    protected AbstractMirrorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected List<Vector3d> getRedirectedDirection(World world, BlockPos pos, BlockState state, Vector3d original) {
        List<Vector3d> directions = new ArrayList<>();
        Vector3d normal = getFaceNormal(world, pos, state);
        if (normal.dot(original) < 0) {
            directions.add(original.subtract(normal.scale(original.dot(normal) * 2)));
        }
        return directions;
    }

    @Override
    protected Vector3d getOffset(World world, BlockPos pos, Vector3d direction) {
        return direction.scale(OFFSET);
    }

    protected abstract Vector3d getFaceNormal(World world, BlockPos pos, BlockState state);

}
