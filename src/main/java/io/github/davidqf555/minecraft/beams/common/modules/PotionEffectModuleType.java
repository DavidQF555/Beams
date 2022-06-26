package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class PotionEffectModuleType extends ProjectorModuleType {

    private final Effect effect;
    private final int amp, duration;

    public PotionEffectModuleType(Effect effect, int duration, int amp) {
        this.effect = effect;
        this.amp = amp;
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target) {
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new EffectInstance(effect, duration, amp));
        }
    }

}
