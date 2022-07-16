package io.github.davidqf555.minecraft.beams.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class OmnidirectionalProjectorBlock extends AbstractProjectorBlock {

    public OmnidirectionalProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Vector3d getStartOffset(ProjectorTileEntity entity, BlockState state) {
        Vector3d pos = new Vector3d(0.5, 0.5, 0.5);
        if (entity instanceof DirectionalProjectorTileEntity) {
            pos = pos.add(((DirectionalProjectorTileEntity) entity).getDirection().scale(0.25));
        }
        return pos;
    }

    @Override
    protected Vector3d getBeamDirection(ProjectorTileEntity entity, BlockState state) {
        if (entity instanceof DirectionalProjectorTileEntity) {
            return ((DirectionalProjectorTileEntity) entity).getDirection();
        }
        return Vector3d.ZERO;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DirectionalProjectorTileEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext selection) {
        return VoxelShapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(BlockState state, IBlockReader reader, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

}
