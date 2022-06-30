package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class EnderModuleType extends ProjectorModuleType {

    private final double range;

    public EnderModuleType(double range) {
        this.range = range;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target instanceof LivingEntity) {
            double startX = target.getX();
            double startY = target.getY();
            double startZ = target.getZ();
            Random rand = ((LivingEntity) target).getRandom();
            for (int i = 0; i < 16; i++) {
                double endX = target.getX() + (rand.nextDouble() * 2 - 1) * range;
                double endY = MathHelper.clamp(target.getY() + (rand.nextDouble() * 2 - 1) * range, 0, target.level.getHeight() - 1);
                double endZ = target.getZ() + (rand.nextDouble() * 2 - 1) * range;
                if (target.isPassenger()) {
                    target.stopRiding();
                }
                if (((LivingEntity) target).randomTeleport(endX, endY, endZ, true)) {
                    target.level.playSound(null, startX, startY, startZ, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1, 1);
                    target.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1, 1);
                    break;
                }
            }
        }
    }
}
