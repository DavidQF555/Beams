package io.github.davidqf555.minecraft.beams.common.blocks;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractProjectorBlock extends BaseEntityBlock {

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract boolean isActive(BlockState state);

    protected abstract List<BeamEntity> shoot(Level world, BlockPos pos, BlockState state);

    protected Map<ProjectorModuleType, Integer> getModules(Level world, BlockPos pos, BlockState state) {
        return ImmutableMap.of();
    }

    public void updateBeam(Level world, BlockPos pos, BlockState state) {
        removeBeam(world, pos);
        if (isActive(state)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof AbstractProjectorTileEntity) {
                for (BeamEntity beam : shoot(world, pos, state)) {
                    ((AbstractProjectorTileEntity) te).addBeam(beam.getUUID());
                }
            }
        }
    }

    public void removeBeam(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof AbstractProjectorTileEntity) {
            if (world instanceof ServerLevel) {
                for (UUID beam : ((AbstractProjectorTileEntity) te).getBeams()) {
                    Entity entity = ((ServerLevel) world).getEntity(beam);
                    if (entity != null) {
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }
            }
            ((AbstractProjectorTileEntity) te).clearBeams();
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            removeBeam(world, pos);
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public abstract AbstractProjectorTileEntity newBlockEntity(BlockPos pos, BlockState state);

}
