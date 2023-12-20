package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class ProjectorModuleType {

    public void onStart(BeamEntity beam, int amt) {

    }

    public void onEntityTick(BeamEntity beam, Entity target, int amt) {

    }

    public void onBlockTick(BeamEntity beam, BlockPos pos, int amt) {

    }

    public void onCollisionTick(BeamEntity beam, BlockPos pos, int amt) {

    }

    public double getGrowthRate(int amt) {
        return 0;
    }

    public double getStartSizeFactor(int amt) {
        return 1;
    }

    public boolean shouldTickBlocks() {
        return false;
    }

    public boolean shouldTickEntities() {
        return false;
    }

}
