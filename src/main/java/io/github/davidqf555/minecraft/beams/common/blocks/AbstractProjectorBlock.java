package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class AbstractProjectorBlock extends BaseEntityBlock {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, TileEntityRegistry.BEAM_PROJECTOR.get(), ProjectorTileEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult clip) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ProjectorTileEntity) {
                player.openMenu((ProjectorTileEntity) te);
            }
            return InteractionResult.CONSUME;
        }
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
                        return;
                    }
                    te.setChanged();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState old, boolean update) {
        if (state.getValue(TRIGGERED)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null) {
                te.setChanged();
            }
        }
    }

    protected abstract Vec3 getStartOffset(ProjectorTileEntity entity, BlockState state);

    protected abstract Vec3 getBeamDirection(ProjectorTileEntity entity, BlockState state);

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ProjectorTileEntity) {
                ((ProjectorTileEntity) te).removeBeams();
                Containers.dropContents(world, pos, (ProjectorTileEntity) te);
                te.setChanged();
                world.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ProjectorTileEntity) {
                ((ProjectorTileEntity) te).setCustomName(stack.getHoverName());
            }
        }
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
