package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.OmnidirectionalMirrorTileEntity;
import io.github.davidqf555.minecraft.beams.common.blocks.te.RedirectorTileEntity;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.UUID;

public class OmnidirectionalMirrorBlock extends AbstractMirrorBlock implements IPointable {

    private static final VoxelShape VISUAL = Block.box(4, 4, 4, 12, 12, 12);

    public OmnidirectionalMirrorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityRegistry.OMNIDIRECTIONAL_MIRROR.get(), AbstractProjectorTileEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return VISUAL;
    }

    @Override
    protected Vec3 getFaceNormal(Level world, BlockPos pos, BlockState state) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalMirrorTileEntity) {
            return ((OmnidirectionalMirrorTileEntity) te).getNormal();
        }
        return new Vec3(1, 0, 0);
    }

    @Override
    public RedirectorTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OmnidirectionalMirrorTileEntity(pos, state);
    }

    @Nullable
    @Override
    public UUID getConnectionID(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalMirrorTileEntity) {
            return ((OmnidirectionalMirrorTileEntity) te).getUUID();
        }
        return null;
    }

    @Override
    public void onPoint(Level world, BlockPos pos, Vec3 target) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof OmnidirectionalMirrorTileEntity) {
            Vec3 dir = target.subtract(Vec3.atCenterOf(pos)).normalize();
            ((OmnidirectionalMirrorTileEntity) te).setNormal(dir);
            updateBeams(world, pos);
            te.setChanged();
        }
    }

}
