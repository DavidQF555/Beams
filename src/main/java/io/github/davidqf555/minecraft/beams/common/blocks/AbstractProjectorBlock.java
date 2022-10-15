package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.world.IBlockReader;

public abstract class AbstractProjectorBlock extends ContainerBlock {

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract boolean isActive(BlockState state);

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public abstract AbstractProjectorTileEntity newBlockEntity(IBlockReader reader);

}
