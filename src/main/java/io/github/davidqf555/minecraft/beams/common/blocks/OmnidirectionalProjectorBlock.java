package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.UUID;

public class OmnidirectionalProjectorBlock extends ContainerProjectorBlock implements IPointable {

    public OmnidirectionalProjectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Vec3 getStartOffset(Level world, BlockPos pos, BlockState state) {
        BlockEntity te = world.getBlockEntity(pos);
        Vec3 center = new Vec3(0.5, 0.5, 0.5);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            center = center.add(((OmnidirectionalProjectorTileEntity) te).getDirection().scale(0.25));
        }
        return center;
    }

    @Override
    public Vec3 getBeamDirection(Level world, BlockPos pos, BlockState state) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            return ((OmnidirectionalProjectorTileEntity) te).getDirection();
        }
        return Vec3.ZERO;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext selection) {
        return Shapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter reader, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public ContainerProjectorTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OmnidirectionalProjectorTileEntity(pos, state);
    }

    @Nullable
    @Override
    public UUID getConnectionID(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            return ((OmnidirectionalProjectorTileEntity) te).getUUID();
        }
        return null;
    }

    @Override
    public void onPoint(World world, BlockPos pos, Vector3d target) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            Vector3d dir = target.subtract(Vector3d.atCenterOf(pos)).normalize();
            ((OmnidirectionalProjectorTileEntity) te).setDirection(dir);
            te.setChanged();
        }
    }

}
