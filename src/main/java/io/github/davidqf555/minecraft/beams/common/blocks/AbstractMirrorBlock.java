package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.MirrorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractMirrorBlock extends AbstractProjectorBlock implements IBeamCollisionEffect {

    private static final double OFFSET = 0.2;

    protected AbstractMirrorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isActive(BlockState state) {
        return true;
    }

    @Override
    protected List<BeamEntity> shoot(Level world, BlockPos pos, BlockState state) {
        List<BeamEntity> beams = new ArrayList<>();
        for (BeamEntity beam : getHit(world, pos)) {
            Vec3 start = beam.getStart();
            Vec3 end = beam.position();
            Vec3 original = end.subtract(start);
            double length = original.length();
            original = original.scale(1 / length);
            Vec3 dir = getReflectedDirection(world, pos, state, original);
            if (dir != null) {
                double width = beam.getEndWidth();
                double height = beam.getEndHeight();
                double maxLength = beam.getMaxRange() - length - OFFSET;
                Vec3 reflectStart = end.subtract(original.scale(BeamEntity.POKE)).add(dir.scale(OFFSET));
                BeamEntity reflect = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, reflectStart, dir, maxLength, beam.getModules(), width, height, width, height, beam.getUUID());
                if (reflect != null) {
                    beams.add(reflect);
                }
            }
        }
        return beams;
    }

    protected List<BeamEntity> getHit(Level world, BlockPos pos) {
        List<BeamEntity> beams = new ArrayList<>();
        if (world instanceof ServerLevel) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof MirrorTileEntity) {
                for (UUID id : new ArrayList<>(((MirrorTileEntity) te).getHit())) {
                    Entity entity = ((ServerLevel) world).getEntity(id);
                    if (entity instanceof BeamEntity && entity.isAlive()) {
                        beams.add((BeamEntity) entity);
                    } else {
                        ((MirrorTileEntity) te).removeHit(id);
                    }
                }
            }
        }
        return beams;
    }

    @Nullable
    protected Vec3 getReflectedDirection(Level world, BlockPos pos, BlockState state, Vec3 original) {
        Vec3 normal = getFaceNormal(world, pos, state);
        if (normal.dot(original) < 0) {
            return original.subtract(normal.scale(original.dot(normal) * 2));
        }
        return null;
    }

    protected abstract Vec3 getFaceNormal(Level world, BlockPos pos, BlockState state);

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        BlockEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof MirrorTileEntity && !((MirrorTileEntity) te).getBeams().contains(beam.getUUID()) && beam.getParents().stream().noneMatch(parent -> ((MirrorTileEntity) te).getHit().contains(parent)) && ((MirrorTileEntity) te).addHit(beam.getUUID())) {
            te.setChanged();
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        BlockEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof MirrorTileEntity && ((MirrorTileEntity) te).removeHit(beam.getUUID())) {
            te.setChanged();
        }
    }

    @Override
    public MirrorTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MirrorTileEntity(pos, state);
    }

}
