package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ProjectorModuleType extends ForgeRegistryEntry<ProjectorModuleType> {

    public void onStart(BeamEntity beam, int amt) {

    }

    public void onEntityTick(BeamEntity beam, Entity target, int amt) {

    }

    public void onBlockTick(BeamEntity beam, BlockPos pos, int amt) {

    }

    public void onCollisionTick(BeamEntity beam, BlockPos pos, int amt) {

    }

    public double getEndSizeFactor(int amt) {
        return 1;
    }

    public double getStartSizeFactor(int amt) {
        return 1;
    }
}
