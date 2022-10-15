package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean update) {
        super.neighborChanged(state, world, pos, neighborBlock, neighborPos, update);
        if (!world.isClientSide()) {
            boolean triggered = state.getValue(TRIGGERED);
            if (triggered != world.hasNeighborSignal(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof AbstractProjectorTileEntity) {
                    world.setBlock(pos, state.cycle(TRIGGERED), 2);
                    if (triggered) {
                        ((AbstractProjectorTileEntity) te).removeBeam();
                        return;
                    }
                    te.setChanged();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState old, boolean update) {
        if (state.getValue(TRIGGERED)) {
            TileEntity te = world.getBlockEntity(pos);
            if (te != null) {
                te.setChanged();
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState prev = super.getStateForPlacement(context);
        if (prev == null) {
            return null;
        }
        return prev.setValue(TRIGGERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

}
