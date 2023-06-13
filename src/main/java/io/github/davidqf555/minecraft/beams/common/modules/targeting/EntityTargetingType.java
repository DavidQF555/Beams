package io.github.davidqf555.minecraft.beams.common.modules.targeting;

import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Predicate;

public class EntityTargetingType implements TargetingModuleType {

    private final Predicate<Entity> condition;

    public EntityTargetingType(Predicate<Entity> condition) {
        this.condition = condition;
    }

    @Nullable
    @Override
    public Vector3d tick(TurretTileEntity te, double range) {
        World world = te.getLevel();
        AxisAlignedBB bounds = AxisAlignedBB.ofSize(range * 2, range * 2, range * 2).move(te.getBlockPos());
        Vector3d center = Vector3d.atCenterOf(te.getBlockPos());
        return world.getEntities((Entity) null, bounds, condition.and(EntityPredicates.NO_CREATIVE_OR_SPECTATOR).and(entity -> !(entity instanceof BeamEntity))).stream()
                .filter(entity -> entity.distanceToSqr(center) <= range * range)
                .filter(entity -> canSee(world, center, entity))
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
                .map(entity -> entity.getEyePosition(1))
                .orElse(null);
    }

    private boolean canSee(World world, Vector3d start, Entity entity) {
        BlockRayTraceResult result = world.clip(new RayTraceContext(start, entity.getEyePosition(1), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null));
        return result.getType() == RayTraceResult.Type.MISS;
    }

}
