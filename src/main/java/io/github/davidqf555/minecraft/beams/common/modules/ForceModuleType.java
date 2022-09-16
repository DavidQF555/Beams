package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class ForceModuleType extends ProjectorModuleType {

    private final Function<Integer, Double> magnitude;

    public ForceModuleType(Function<Integer, Double> magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target.isPushable()) {
            Vec3 force = beam.position().subtract(beam.getStart()).normalize().scale(magnitude.apply(amt));
            target.push(force.x(), force.y(), force.z());
            target.hurtMarked = true;
        }
    }
}
