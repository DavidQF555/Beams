package io.github.davidqf555.minecraft.beams.common.blocks;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class DirectedProjectorBlock extends AbstractProjectorBlock {

    protected DirectedProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract Vector3d getStartOffset(World world, BlockPos pos, BlockState state);

    public abstract Vector3d getBeamDirection(World world, BlockPos pos, BlockState state);

    @Override
    public List<BeamEntity> shoot(World world, BlockPos pos, BlockState state) {
        Vector3d dir = getBeamDirection(world, pos, state);
        Vector3d start = Vector3d.atLowerCornerOf(pos).add(getStartOffset(world, pos, state));
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        BeamEntity beam = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, start, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), getModules(world, pos, state), size, size, size, size);
        if (beam == null) {
            return ImmutableList.of();
        }
        return ImmutableList.of(beam);
    }

}
