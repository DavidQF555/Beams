package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;

public class FreezeModuleType extends ProjectorModuleType {

    private final Function<Integer, Integer> amp, duration;

    public FreezeModuleType(Function<Integer, Integer> amp, Function<Integer, Integer> duration) {
        this.amp = amp;
        this.duration = duration;
    }

    @Override
    public void onEntityTick(BeamEntity beam, Entity target, int amt) {
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, duration.apply(amt), amp.apply(amt) - 1));
            ((LivingEntity) target).addEffect(new EffectInstance(Effects.DIG_SLOWDOWN, duration.apply(amt), amp.apply(amt) - 1));
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
            beam.level.getBlockTicks().scheduleTick(pos, Blocks.FROSTED_ICE, MathHelper.nextInt(beam.level.getRandom(), 60, 120));
        }
    }

}
