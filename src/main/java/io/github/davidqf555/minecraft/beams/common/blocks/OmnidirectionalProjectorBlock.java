package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.ContainerProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), AbstractProjectorTileEntity::tick);
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
    public UUID getConnectionID(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            return ((OmnidirectionalProjectorTileEntity) te).getUUID();
        }
        return null;
    }

    @Override
    public void onPoint(Level world, BlockPos pos, Vec3 target) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            Vec3 dir = target.subtract(Vec3.atCenterOf(pos)).normalize();
            ((OmnidirectionalProjectorTileEntity) te).setDirection(dir);
            updateBeams(world, pos);
            te.setChanged();
        }
    }

}
