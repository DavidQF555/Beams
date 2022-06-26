package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class ProjectorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public ProjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean update) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, update);
        if (!world.isClientSide()) {
            boolean triggered = state.getValue(TRIGGERED);
            if (triggered != world.hasNeighborSignal(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
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

    protected Vec3 getStartOffset(BlockState state) {
        return Vec3.atLowerCornerOf(state.getValue(FACING).getNormal()).scale(0.5).add(0.5, 0.5, 0.5);
    }

    protected Vec3 getBeamDirection(BlockState state) {
        return Vec3.atLowerCornerOf(state.getValue(FACING).getNormal());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ProjectorTileEntity) {
                ((ProjectorTileEntity) te).removeBeams();
            }
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ProjectorTileEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
}
