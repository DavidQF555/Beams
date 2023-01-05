package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.MirrorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
    public List<BeamEntity> shoot(World world, BlockPos pos, BlockState state) {
        List<BeamEntity> beams = new ArrayList<>();
        for (BeamEntity beam : getHit(world, pos)) {
            Vector3d start = beam.position();
            Vector3d end = beam.getEnd();
            Vector3d original = end.subtract(start);
            double length = original.length();
            original = original.scale(1 / length);
            Vector3d dir = getReflectedDirection(world, pos, state, original);
            if (dir != null) {
                double width = beam.getEndWidth();
                double height = beam.getEndHeight();
                double maxLength = beam.getMaxRange() - length - OFFSET;
                Vector3d reflectStart = end.subtract(original.scale(BeamEntity.POKE)).add(dir.scale(OFFSET));
                BeamEntity reflect = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, reflectStart, dir, maxLength, beam.getModules(), width, height, width, height, beam.getUUID());
                if (reflect != null) {
                    beams.add(reflect);
                }
            }
        }
        return beams;
    }

    protected List<BeamEntity> getHit(World world, BlockPos pos) {
        List<BeamEntity> beams = new ArrayList<>();
        if (world instanceof ServerWorld) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof MirrorTileEntity) {
                for (UUID id : new ArrayList<>(((MirrorTileEntity) te).getHit())) {
                    Entity entity = ((ServerWorld) world).getEntity(id);
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
    protected Vector3d getReflectedDirection(World world, BlockPos pos, BlockState state, Vector3d original) {
        Vector3d normal = getFaceNormal(world, pos, state);
        if (normal.dot(original) < 0) {
            return original.subtract(normal.scale(original.dot(normal) * 2));
        }
        return null;
    }

    protected abstract Vector3d getFaceNormal(World world, BlockPos pos, BlockState state);

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof MirrorTileEntity && !((MirrorTileEntity) te).getBeams().contains(beam.getUUID()) && beam.getParents().stream().noneMatch(parent -> ((MirrorTileEntity) te).getHit().contains(parent)) && ((MirrorTileEntity) te).addHit(beam.getUUID())) {
            updateBeams(beam.level, pos);
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof MirrorTileEntity && ((MirrorTileEntity) te).removeHit(beam.getUUID())) {
            updateBeams(beam.level, pos);
        }
    }

    @Override
    public MirrorTileEntity newBlockEntity(IBlockReader reader) {
        return new MirrorTileEntity();
    }

}
