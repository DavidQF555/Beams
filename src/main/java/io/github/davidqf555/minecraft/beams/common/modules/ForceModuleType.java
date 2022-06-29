package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

public class ForceModuleType extends ProjectorModuleType {

    private final int period;
    private final double magnitude;

    public ForceModuleType(int period, double magnitude) {
        this.magnitude = magnitude;
        this.period = period;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target) {
        if (target.level.getGameTime() % period == 0 && target.isPushable()) {
            Vector3d force = beam.position().subtract(beam.getStart()).normalize().scale(magnitude);
            target.push(force.x(), force.y(), force.z());
        }
    }
}
