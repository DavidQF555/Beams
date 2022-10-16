package io.github.davidqf555.minecraft.beams.common.blocks.te;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.DirectedProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Map;

public class DirectedProjectorTileEntity extends AbstractProjectorTileEntity {

    public DirectedProjectorTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    protected void shoot() {
        World world = getLevel();
        BlockPos pos = getBlockPos();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        Vector3d dir = ((DirectedProjectorBlock) block).getBeamDirection(this, state);
        Vector3d start = Vector3d.atLowerCornerOf(pos).add(((DirectedProjectorBlock) block).getStartOffset(this, state));
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        BeamEntity entity = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, start, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), getModules(), size, size, size, size);
        beam = entity == null ? null : entity.getUUID();
    }

    protected Map<ProjectorModuleType, Integer> getModules() {
        return ImmutableMap.of();
    }

}
