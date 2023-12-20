package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class PortableProjectorItem extends ShootableItem {

    public PortableProjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int duration) {
        int time = getUseDuration(stack) - duration;
        double range = getRange(time);
        if (range > 0) {
            BeamEntity beam = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, entity.getEyePosition(1), entity.getLookAngle(), range, ProjectorInventory.getModuleTypes(ProjectorInventory.get(stack)), 0.5, 0.5, 0.5, 0.5, null, null);
            if (beam != null) {
                beam.setLifespan(20);
                beam.setShooter(entity.getUUID());
            }
        }
    }

    protected double getRange(int time) {
        return time < 20 ? 0 : getDefaultProjectileRange() * Math.min(1, time / 200.0);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (world.isClientSide()) {
                return ActionResult.success(stack);
            }
            player.openMenu(ProjectorInventory.get(stack));
        } else {
            player.startUsingItem(hand);
        }
        return ActionResult.consume(stack);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> false;
    }

    @Override
    public int getDefaultProjectileRange() {
        return ServerConfigs.INSTANCE.portableProjectorMaxRange.get();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ProjectorInventory.Provider();
    }
}
