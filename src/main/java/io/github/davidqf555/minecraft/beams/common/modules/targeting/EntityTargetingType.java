package io.github.davidqf555.minecraft.beams.common.modules.targeting;

import io.github.davidqf555.minecraft.beams.common.blocks.te.TurretTileEntity;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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
    public Vec3 tick(TurretTileEntity te, double range) {
        Level world = te.getLevel();
        AABB bounds = AABB.ofSize(Vec3.atLowerCornerOf(te.getBlockPos()), range * 2, range * 2, range * 2);
        Vec3 center = Vec3.atCenterOf(te.getBlockPos());
        return world.getEntities((Entity) null, bounds, condition.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR).and(entity -> !(entity instanceof BeamEntity))).stream()
                .filter(entity -> entity.distanceToSqr(center) <= range * range)
                .filter(entity -> canSee(world, center, entity))
                .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)))
                .map(entity -> entity.getEyePosition(1))
                .orElse(null);
    }

    private boolean canSee(Level world, Vec3 start, Entity entity) {
        BlockHitResult result = world.clip(new ClipContext(start, entity.getEyePosition(1), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
        return result.getType() == HitResult.Type.MISS;
    }

}
