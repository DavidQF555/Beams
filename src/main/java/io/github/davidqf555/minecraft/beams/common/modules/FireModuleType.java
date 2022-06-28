package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;

public class FireModuleType extends ProjectorModuleType {

    private final int duration;

    public FireModuleType(int duration) {
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target) {
        target.setSecondsOnFire(duration);
    }
}
