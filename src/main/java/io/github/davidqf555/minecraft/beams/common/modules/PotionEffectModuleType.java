package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PotionEffectModuleType extends ProjectorModuleType {

    private final MobEffect effect;
    private final int amp, duration;

    public PotionEffectModuleType(MobEffect effect, int duration, int amp) {
        this.effect = effect;
        this.amp = amp;
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target) {
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new MobEffectInstance(effect, duration, amp));
        }
    }

}
