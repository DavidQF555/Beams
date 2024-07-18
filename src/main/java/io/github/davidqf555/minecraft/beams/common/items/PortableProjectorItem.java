package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class PortableProjectorItem extends Item {

    private final static Component INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".portable_projector.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);

    public PortableProjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        text.add(INSTRUCTIONS);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int duration) {
        int time = getUseDuration(stack) - duration;
        double range = getRange(time);
        if (range > 0) {
            BeamEntity beam = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, entity.getEyePosition(1), entity.getLookAngle(), range, ProjectorInventory.getModuleTypes(ProjectorInventory.get(stack)), 0.5, 0.5, null, null);
            if (beam != null) {
                beam.setLifespan(20);
                beam.setShooter(entity.getUUID());
            }
        }
    }

    protected double getRange(int time) {
        return time < 20 ? 0 : ServerConfigs.INSTANCE.portableProjectorMaxRange.get() * Math.min(1, time / 200.0);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (world.isClientSide()) {
                return InteractionResultHolder.success(stack);
            }
            player.openMenu(ProjectorInventory.get(stack));
        } else {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ProjectorInventory.Provider();
    }
}
