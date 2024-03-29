package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;

public abstract class AbstractProjectorBlock extends ContainerBlock {

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract boolean isActive(BlockState state);

    public abstract List<BeamEntity> shoot(World world, BlockPos pos, BlockState state);

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, World world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof AbstractProjectorTileEntity) {
                ((AbstractProjectorTileEntity) te).clearBeams();
            }
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    protected void updateBeams(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof AbstractProjectorTileEntity) {
            ((AbstractProjectorTileEntity) te).markChanged();
        }
    }

    @Override
    public abstract AbstractProjectorTileEntity newBlockEntity(IBlockReader reader);

}
