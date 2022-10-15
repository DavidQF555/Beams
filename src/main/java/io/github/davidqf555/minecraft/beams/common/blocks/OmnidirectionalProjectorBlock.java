package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.DirectedProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class OmnidirectionalProjectorBlock extends ContainerProjectorBlock {

    public OmnidirectionalProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Vector3d getStartOffset(DirectedProjectorTileEntity entity, BlockState state) {
        Vector3d pos = new Vector3d(0.5, 0.5, 0.5);
        if (entity instanceof OmnidirectionalProjectorTileEntity) {
            pos = pos.add(((OmnidirectionalProjectorTileEntity) entity).getDirection().scale(0.25));
        }
        return pos;
    }

    @Override
    public Vector3d getBeamDirection(DirectedProjectorTileEntity entity, BlockState state) {
        if (entity instanceof OmnidirectionalProjectorTileEntity) {
            return ((OmnidirectionalProjectorTileEntity) entity).getDirection();
        }
        return Vector3d.ZERO;
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

    @Override
    public ContainerProjectorTileEntity newBlockEntity(IBlockReader reader) {
        return new OmnidirectionalProjectorTileEntity();
    }
}
