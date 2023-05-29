package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.PointableProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class PointableProjectorBlock extends OmnidirectionalProjectorBlock implements IPointable {

    public PointableProjectorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), AbstractProjectorTileEntity::tick);
    }

    @Nullable
    @Override
    public UUID getConnectionID(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof PointableProjectorTileEntity) {
            return ((PointableProjectorTileEntity) te).getUUID();
        }
        return null;
    }

    @Override
    public void onPoint(Level world, BlockPos pos, Vec3 target) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalProjectorTileEntity) {
            Vec3 dir = target.subtract(Vec3.atCenterOf(pos));
            ((OmnidirectionalProjectorTileEntity) te).setDirection(dir);
            updateBeams(world, pos);
            te.setChanged();
        }
    }

    @Override
    public PointableProjectorTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PointableProjectorTileEntity(pos, state);
    }
}
