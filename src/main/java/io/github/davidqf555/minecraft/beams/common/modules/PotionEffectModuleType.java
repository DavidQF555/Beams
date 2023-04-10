package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class PotionEffectModuleType extends ProjectorModuleType {

    private final MobEffect effect;
    private final Function<Integer, Integer> amp, duration;

    public PotionEffectModuleType(MobEffect effect, Function<Integer, Integer> duration, Function<Integer, Integer> amp) {
        this.effect = effect;
        this.amp = amp;
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new MobEffectInstance(effect, duration.apply(amt), amp.apply(amt)));
        }
    }

    @Override
    public boolean shouldTickEntities() {
        return true;
    }
}
