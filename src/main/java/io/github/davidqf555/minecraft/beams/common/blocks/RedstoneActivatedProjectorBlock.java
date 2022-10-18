package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public abstract class RedstoneActivatedProjectorBlock extends DirectedProjectorBlock {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    protected RedstoneActivatedProjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, false));
    }

    @Override
    public boolean isActive(BlockState state) {
        return state.getValue(TRIGGERED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean update) {
        if (!world.isClientSide()) {
            boolean triggered = state.getValue(TRIGGERED);
            if (triggered != world.hasNeighborSignal(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof AbstractProjectorTileEntity) {
                    world.setBlockAndUpdate(pos, state.cycle(TRIGGERED));
                }
            }
        }
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, update);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState old, boolean update) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null) {
            te.setChanged();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState prev = super.getStateForPlacement(context);
        if (prev == null) {
            return null;
        }
        return prev.setValue(TRIGGERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

}
