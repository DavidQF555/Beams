package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Function;

public class ForceModuleType extends ProjectorModuleType {

    private final Function<Integer, Double> magnitude;

    public ForceModuleType(Function<Integer, Double> magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        Vector3d force = beam.getEnd().subtract(beam.position()).normalize().scale(magnitude.apply(amt));
        target.push(force.x(), force.y(), force.z());
        target.hurtMarked = true;
    }

    @Override
    public boolean shouldTickEntities() {
        return true;
    }

}
