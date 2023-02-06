package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Function;

public class FreezeModuleType extends ProjectorModuleType {

    private final Function<Integer, Integer> amp, duration, freeze;

    public FreezeModuleType(Function<Integer, Integer> amp, Function<Integer, Integer> duration, Function<Integer, Integer> freeze) {
        this.amp = amp;
        this.duration = duration;
        this.freeze = freeze;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target.canFreeze()) {
            target.setTicksFrozen(target.getTicksFrozen() + freeze.apply(amt) + 2);
        }
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration.apply(amt), amp.apply(amt) - 1));
            ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration.apply(amt), amp.apply(amt) - 1));
        }
    }

    @Override
    public void onBlockTick(BeamEntity beam, BlockPos pos, int amt) {
        if (beam.level.isEmptyBlock(pos)) {
            BlockState snow = Blocks.SNOW.defaultBlockState();
            if (snow.canSurvive(beam.level, pos)) {
                beam.level.setBlockAndUpdate(pos, snow);
            }
        } else if (beam.level.getFluidState(pos).getType().equals(Fluids.WATER)) {
            beam.level.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
            beam.level.scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(beam.level.getRandom(), 60, 120));
        }
    }

}
