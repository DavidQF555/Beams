package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;
import java.util.function.Function;

public class DamageModuleType extends ProjectorModuleType {

    private final Function<Integer, Float> damage;
    private final int period;

    public DamageModuleType(int period, Function<Integer, Float> damage) {
        this.period = period;
        this.damage = damage;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target instanceof LivingEntity && target.level.getGameTime() % period == 0) {
            LivingEntity shooter = null;
            UUID id = beam.getShooter();
            if (id != null) {
                Entity entity = ((ServerLevel) beam.level).getEntity(id);
                if (entity instanceof LivingEntity) {
                    shooter = (LivingEntity) entity;
                }
            }
            target.hurt(beam.level.damageSources().mobProjectile(beam, shooter), damage.apply(amt));
        }
    }
}
