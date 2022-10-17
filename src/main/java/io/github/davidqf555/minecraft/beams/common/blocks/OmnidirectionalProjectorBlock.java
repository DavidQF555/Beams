package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class OmnidirectionalProjectorBlock extends ContainerProjectorBlock {

    public OmnidirectionalProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Vector3d getStartOffset(World world, BlockPos pos, BlockState state) {
        TileEntity te = world.getBlockEntity(pos);
        Vector3d center = new Vector3d(0.5, 0.5, 0.5);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            center = center.add(((OmnidirectionalProjectorTileEntity) te).getDirection().scale(0.25));
        }
        return center;
    }

    @Override
    public Vector3d getBeamDirection(World world, BlockPos pos, BlockState state) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            return ((OmnidirectionalProjectorTileEntity) te).getDirection();
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
