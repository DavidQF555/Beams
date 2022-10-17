package io.github.davidqf555.minecraft.beams.common.blocks;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.beams.common.blocks.te.AbstractProjectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractProjectorBlock extends ContainerBlock {

    protected AbstractProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract boolean isActive(BlockState state);

    protected abstract List<BeamEntity> shoot(World world, BlockPos pos, BlockState state);

    protected Map<ProjectorModuleType, Integer> getModules(World world, BlockPos pos, BlockState state) {
        return ImmutableMap.of();
    }

    public void updateBeam(World world, BlockPos pos, BlockState state) {
        removeBeam(world, pos);
        if (isActive(state)) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof AbstractProjectorTileEntity) {
                for (BeamEntity beam : shoot(world, pos, state)) {
                    ((AbstractProjectorTileEntity) te).addBeam(beam.getUUID());
                }
            }
        }
    }

    public void removeBeam(World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof AbstractProjectorTileEntity) {
            if (world instanceof ServerWorld) {
                for (UUID beam : ((AbstractProjectorTileEntity) te).getBeams()) {
                    Entity entity = ((ServerWorld) world).getEntity(beam);
                    if (entity != null) {
                        entity.remove();
                    }
                }
            }
            ((AbstractProjectorTileEntity) te).clearBeams();
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state1, World world, BlockPos pos, BlockState state2, boolean update) {
        if (!state1.is(state2.getBlock())) {
            removeBeam(world, pos);
        }
        super.onRemove(state1, world, pos, state2, update);
    }

    @Override
    public abstract AbstractProjectorTileEntity newBlockEntity(IBlockReader reader);

}
