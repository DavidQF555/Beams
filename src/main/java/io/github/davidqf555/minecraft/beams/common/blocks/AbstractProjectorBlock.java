package io.github.davidqf555.minecraft.beams.common.blocks;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class AbstractProjectorBlock extends BaseEntityBlock {

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract boolean isActive(BlockState state);

    public abstract List<BeamEntity> shoot(Level world, BlockPos pos, BlockState state);

    protected Map<ProjectorModuleType, Integer> getModules(Level world, BlockPos pos, BlockState state) {
        return ImmutableMap.of();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type);

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, Level world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof AbstractProjectorTileEntity) {
                ((AbstractProjectorTileEntity) te).clearBeams();
            }
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    protected void updateBeams(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof AbstractProjectorTileEntity) {
            ((AbstractProjectorTileEntity) te).markChanged();
        }
    }

    @Override
    public abstract AbstractProjectorTileEntity newBlockEntity(BlockPos pos, BlockState state);

}
