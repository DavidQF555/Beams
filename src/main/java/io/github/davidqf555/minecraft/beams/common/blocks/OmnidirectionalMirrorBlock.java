package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.PointableRedirectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.RedirectorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class OmnidirectionalMirrorBlock extends AbstractMirrorBlock implements IPointable {

    private static final VoxelShape VISUAL = Block.box(4, 4, 4, 12, 12, 12);

    public OmnidirectionalMirrorBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return VISUAL;
    }

    @Override
    protected Vector3d getFaceNormal(World world, BlockPos pos, BlockState state) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PointableRedirectorTileEntity) {
            return ((PointableRedirectorTileEntity) te).getNormal();
        }
        return new Vector3d(1, 0, 0);
    }

    @Override
    public RedirectorTileEntity newBlockEntity(IBlockReader reader) {
        return new PointableRedirectorTileEntity();
    }

    @Nullable
    @Override
    public UUID getConnectionID(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PointableRedirectorTileEntity) {
            return ((PointableRedirectorTileEntity) te).getUUID();
        }
        return null;
    }

    @Override
    public void onPoint(World world, BlockPos pos, Vector3d target) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PointableRedirectorTileEntity) {
            Vector3d dir = target.subtract(Vector3d.atCenterOf(pos)).normalize();
            ((PointableRedirectorTileEntity) te).setNormal(dir);
            updateBeams(world, pos);
            te.setChanged();
        }
    }

}
