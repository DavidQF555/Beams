package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;

public abstract class ContainerProjectorBlock extends RedstoneActivatedProjectorBlock {

    protected ContainerProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ContainerProjectorTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ContainerProjectorTileEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult clip) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ContainerProjectorTileEntity) {
                player.openMenu((ContainerProjectorTileEntity) te);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ContainerProjectorTileEntity) {
                Containers.dropContents(world, pos, (ContainerProjectorTileEntity) te);
            }
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ContainerProjectorTileEntity) {
                ((ContainerProjectorTileEntity) te).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    protected Map<ProjectorModuleType, Integer> getModules(Level world, BlockPos pos, BlockState state) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ContainerProjectorTileEntity) {
            return ((ContainerProjectorTileEntity) te).getModules();
        }
        return super.getModules(world, pos, state);
    }

}
