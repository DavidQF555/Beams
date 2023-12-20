package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.blocks.te.RedirectorTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
    public List<BeamEntity> shoot(World world, BlockPos pos, BlockState state) {
        List<BeamEntity> beams = new ArrayList<>();
        for (BeamEntity beam : getHit(world, pos)) {
            Vector3d start = beam.position();
            Vector3d end = beam.getEnd();
            Vector3d original = end.subtract(start);
            double length = original.length();
            original = original.scale(1 / length);
            List<Vector3d> directions = getRedirectedDirection(world, pos, state, original);
            for (Vector3d dir : directions) {
                double width = beam.getEndWidth() * getRedirectWidthFactor();
                double height = beam.getEndHeight() * getRedirectHeightFactor();
                double maxLength = beam.getMaxRange() - length;
                Vector3d redirectStart = end.subtract(original.scale(BeamEntity.POKE)).add(getOffset(world, pos, dir));
                BeamEntity redirect = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, redirectStart, dir, maxLength, getRedirectedModules(dir, beam.getModules()), width, height, width, height, beam.getUUID(), pos);
                if (redirect != null) {
                    beams.add(redirect);
                }
            }
        }
        return beams;
    }

    protected List<BeamEntity> getHit(World world, BlockPos pos) {
        List<BeamEntity> beams = new ArrayList<>();
        if (world instanceof ServerWorld) {
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof RedirectorTileEntity) {
                for (UUID id : new ArrayList<>(((RedirectorTileEntity) te).getHit())) {
                    Entity entity = ((ServerWorld) world).getEntity(id);
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

    protected abstract List<Vector3d> getRedirectedDirection(World world, BlockPos pos, BlockState state, Vector3d original);

    protected abstract Vector3d getOffset(World world, BlockPos pos, Vector3d direction);

    protected Map<ProjectorModuleType, Integer> getRedirectedModules(Vector3d dir, Map<ProjectorModuleType, Integer> modules) {
        return modules;
    }

    @Override
    public void onBeamStartCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof RedirectorTileEntity && !((RedirectorTileEntity) te).getBeams().contains(beam.getUUID()) && beam.getParents().stream().noneMatch(parent -> ((RedirectorTileEntity) te).getHit().contains(parent)) && ((RedirectorTileEntity) te).addHit(beam.getUUID())) {
            updateBeams(beam.level, pos);
        }
    }

    @Override
    public void onBeamStopCollision(BeamEntity beam, BlockPos pos, BlockState state) {
        TileEntity te = beam.level.getBlockEntity(pos);
        if (te instanceof RedirectorTileEntity && ((RedirectorTileEntity) te).removeHit(beam.getUUID())) {
            updateBeams(beam.level, pos);
        }
    }

    @Override
    public RedirectorTileEntity newBlockEntity(IBlockReader reader) {
        return new RedirectorTileEntity();
    }

    protected double getRedirectWidthFactor() {
        return 1;
    }

    protected double getRedirectHeightFactor() {
        return 1;
    }

}
