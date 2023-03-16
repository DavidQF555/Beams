package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMirrorBlock extends AbstractRedirectorBlock {

    protected static final double OFFSET = 0.2;

    protected AbstractMirrorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected List<Vec3> getRedirectedDirection(Level world, BlockPos pos, BlockState state, Vec3 original) {
        List<Vec3> directions = new ArrayList<>();
        Vec3 normal = getFaceNormal(world, pos, state);
        if (normal.dot(original) < 0) {
            directions.add(original.subtract(normal.scale(original.dot(normal) * 2)));
        }
        return directions;
    }

    @Override
    protected Vec3 getOffset(Level world, BlockPos pos, Vec3 direction) {
        return direction.scale(OFFSET);
    }

    protected abstract Vec3 getFaceNormal(Level world, BlockPos pos, BlockState state);

}
