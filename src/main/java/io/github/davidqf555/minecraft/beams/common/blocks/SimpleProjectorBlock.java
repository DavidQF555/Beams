package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.DirectedProjectorTileEntity;
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

public class SimpleProjectorBlock extends ContainerProjectorBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SimpleProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Vector3d getStartOffset(DirectedProjectorTileEntity entity, BlockState state) {
        return Vector3d.atLowerCornerOf(state.getValue(FACING).getNormal()).scale(0.5).add(0.5, 0.5, 0.5);
    }

    @Override
    public Vector3d getBeamDirection(DirectedProjectorTileEntity entity, BlockState state) {
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
