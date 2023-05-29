package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.PointableProjectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class PointableProjectorBlock extends OmnidirectionalProjectorBlock implements IPointable {

    public PointableProjectorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public UUID getConnectionID(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PointableProjectorTileEntity) {
            return ((PointableProjectorTileEntity) te).getUUID();
        }
        return null;
    }

    @Override
    public void onPoint(World world, BlockPos pos, Vector3d target) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            Vector3d dir = target.subtract(Vector3d.atCenterOf(pos));
            ((OmnidirectionalProjectorTileEntity) te).setDirection(dir);
            updateBeams(world, pos);
            te.setChanged();
        }
    }

    @Override
    public PointableProjectorTileEntity newBlockEntity(IBlockReader reader) {
        return new PointableProjectorTileEntity();
    }
}
