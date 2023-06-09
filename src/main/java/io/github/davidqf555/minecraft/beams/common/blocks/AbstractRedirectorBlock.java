package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.RedirectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractRedirectorBlock extends AbstractProjectorBlock implements IBeamCollisionEffect {

    protected AbstractRedirectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isActive(BlockState state) {
        return true;
    }

    @Override
    public List<BeamEntity> shoot(Level world, BlockPos pos, BlockState state) {
        List<BeamEntity> beams = new ArrayList<>();
        for (BeamEntity beam : getHit(world, pos)) {
            Vec3 start = beam.position();
            Vec3 end = beam.getEnd();
            Vec3 original = end.subtract(start);
            double length = original.length();
            original = original.scale(1 / length);
            List<Vec3> directions = getRedirectedDirection(world, pos, state, original);
            for (Vec3 dir : directions) {
                double width = beam.getEndWidth() * getRedirectWidthFactor();
                double height = beam.getEndHeight() * getRedirectHeightFactor();
                double maxLength = beam.getMaxRange() - length;
                Vec3 redirectStart = end.subtract(original.scale(BeamEntity.POKE)).add(getOffset(world, pos, dir));
                BeamEntity redirect = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, redirectStart, dir, maxLength, getRedirectedModules(dir, beam.getModules()), width, height, width, height, beam.getUUID());
                if (redirect != null) {
                    beams.add(redirect);
                }
            }
        }
        return beams;
    }

    protected List<BeamEntity> getHit(Level world, BlockPos pos) {
        List<BeamEntity> beams = new ArrayList<>();
        if (world instanceof ServerLevel) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof RedirectorTileEntity) {
                for (UUID id : new ArrayList<>(((RedirectorTileEntity) te).getHit())) {
                    Entity entity = ((ServerLevel) world).getEntity(id);
                    if (entity instanceof BeamEntity && entity.isAlive()) {
                        beams.add((BeamEntity) entity);
                    } else {
                        ((RedirectorTileEntity) te).removeHit(id);
                    }
                }
            }
        }
        return beams;
    }

    protected abstract List<Vec3> getRedirectedDirection(Level world, BlockPos pos, BlockState state, Vec3 original);

    protected abstract Vec3 getOffset(Level world, BlockPos pos, Vec3 direction);

    protected Map<ProjectorModuleType, Integer> getRedirectedModules(Vec3 dir, Map<ProjectorModuleType, Integer> modules) {
        return modules;
    }

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        BlockEntity te = beam.level().getBlockEntity(pos);
        if (te instanceof RedirectorTileEntity && !((RedirectorTileEntity) te).getBeams().contains(beam.getUUID()) && beam.getParents().stream().noneMatch(parent -> ((RedirectorTileEntity) te).getHit().contains(parent)) && ((RedirectorTileEntity) te).addHit(beam.getUUID())) {
            updateBeams(beam.level(), pos);
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        BlockEntity te = beam.level().getBlockEntity(pos);
        if (te instanceof RedirectorTileEntity && ((RedirectorTileEntity) te).removeHit(beam.getUUID())) {
            updateBeams(beam.level(), pos);
        }
    }

    @Override
    public RedirectorTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedirectorTileEntity(pos, state);
    }

    protected double getRedirectWidthFactor() {
        return 1;
    }

    protected double getRedirectHeightFactor() {
        return 1;
    }

}
