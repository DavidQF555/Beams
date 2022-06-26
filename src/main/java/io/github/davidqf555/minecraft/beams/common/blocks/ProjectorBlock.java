package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ProjectorBlock extends ContainerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public ProjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean update) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, update);
        if (!world.isClientSide()) {
            boolean triggered = state.getValue(TRIGGERED);
            if (triggered != world.hasNeighborSignal(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof ProjectorTileEntity) {
                    world.setBlock(pos, state.cycle(TRIGGERED), 2);
                    if (triggered) {
                        ((ProjectorTileEntity) te).removeBeams();
                    } else {
                        ((ProjectorTileEntity) te).updateBeams();
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState old, boolean update) {
        if (state.getValue(TRIGGERED)) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof ProjectorTileEntity) {
                ((ProjectorTileEntity) te).updateBeams();
            }
        }
    }

    protected Vector3d getStartOffset(BlockState state) {
        return Vector3d.atLowerCornerOf(state.getValue(FACING).getNormal()).scale(0.5).add(0.5, 0.5, 0.5);
    }

    protected Vector3d getBeamDirection(BlockState state) {
        return Vector3d.atLowerCornerOf(state.getValue(FACING).getNormal());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, World world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof ProjectorTileEntity) {
                ((ProjectorTileEntity) te).removeBeams();
            }
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return new ProjectorTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
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

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(TRIGGERED, context.getLevel().hasNeighborSignal(context.getClickedPos())).setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
}
