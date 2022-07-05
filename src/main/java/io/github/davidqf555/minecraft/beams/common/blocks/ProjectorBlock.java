package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class ProjectorBlock extends AbstractProjectorBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public ProjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
    }

    @Override
    protected Vector3d getStartOffset(BlockState state) {
        return Vector3d.atLowerCornerOf(state.getValue(FACING).getNormal()).scale(0.5).add(0.5, 0.5, 0.5);
    }

    @Override
    protected Vector3d getBeamDirection(BlockState state) {
        return Vector3d.atLowerCornerOf(state.getValue(FACING).getNormal());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState prev = super.getStateForPlacement(context);
        if (prev == null) {
            return null;
        }
        return prev.setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
}
