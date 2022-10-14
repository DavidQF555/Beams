package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.ProjectorTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public abstract class AbstractProjectorBlock extends ContainerBlock {

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract Vector3d getStartOffset(ProjectorTileEntity entity, BlockState state);

    public abstract Vector3d getBeamDirection(ProjectorTileEntity entity, BlockState state);

    public abstract boolean isActive(BlockState state);

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public abstract ProjectorTileEntity newBlockEntity(IBlockReader reader);

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return super.createTileEntity(state, world);
    }
}
