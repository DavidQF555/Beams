package io.github.davidqf555.minecraft.beams.common.blocks;

import com.google.common.collect.ImmutableList;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class DirectedProjectorBlock extends AbstractProjectorBlock {

    protected DirectedProjectorBlock(Properties properties) {
        super(properties);
    }

    public abstract Vec3 getStartOffset(Level world, BlockPos pos, BlockState state);

    public abstract Vec3 getBeamDirection(Level world, BlockPos pos, BlockState state);

    @Override
    public List<BeamEntity> shoot(Level world, BlockPos pos, BlockState state) {
        Vec3 dir = getBeamDirection(world, pos, state);
        Vec3 start = Vec3.atLowerCornerOf(pos).add(getStartOffset(world, pos, state));
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        BeamEntity beam = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, start, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), getModules(world, pos, state), size, size, size, size, null);
        if (beam == null) {
            return ImmutableList.of();
        }
        return ImmutableList.of(beam);
    }

}
