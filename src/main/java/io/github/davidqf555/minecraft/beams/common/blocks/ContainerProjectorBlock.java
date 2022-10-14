package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class ContainerProjectorBlock extends RedstoneActivatedProjectorBlock {

    protected ContainerProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ContainerProjectorTileEntity newBlockEntity(IBlockReader reader) {
        return new ContainerProjectorTileEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult clip) {
        if (world.isClientSide()) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof ContainerProjectorTileEntity) {
                player.openMenu((ContainerProjectorTileEntity) te);
            }
            return ActionResultType.CONSUME;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, World world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof ContainerProjectorTileEntity) {
                ((ContainerProjectorTileEntity) te).removeBeams();
                InventoryHelper.dropContents(world, pos, (ContainerProjectorTileEntity) te);
            }
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof ContainerProjectorTileEntity) {
                ((ContainerProjectorTileEntity) te).setCustomName(stack.getHoverName());
            }
        }
    }
}
