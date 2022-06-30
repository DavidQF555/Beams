package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.function.Function;

public class PotionEffectModuleType extends ProjectorModuleType {

    private final Effect effect;
    private final Function<Integer, Integer> amp, duration;

    public PotionEffectModuleType(Effect effect, Function<Integer, Integer> duration, Function<Integer, Integer> amp) {
        this.effect = effect;
        this.amp = amp;
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new EffectInstance(effect, duration.apply(amt), amp.apply(amt)));
        }
    }

}
