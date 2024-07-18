package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PortableProjectorItem extends Item {

    private final static Component INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".portable_projector.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);
    public static final DataComponentType<ItemContainerContents> CONTENTS = DataComponents.CONTAINER;

    public PortableProjectorItem(Properties properties) {
        super(properties.component(CONTENTS, ItemContainerContents.EMPTY));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        text.add(INSTRUCTIONS);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int duration) {
        int time = getUseDuration(stack) - duration;
        double range = getRange(time);
        if (range > 0) {
            ItemContainerContents contents = stack.get(CONTENTS);
            if (contents == null) {
                contents = ItemContainerContents.EMPTY;
            }
            BeamEntity beam = BeamEntity.shoot(EntityRegistry.BEAM.get(), world, entity.getEyePosition(1), entity.getLookAngle(), range, ProjectorModuleType.getModuleTypes(contents.nonEmptyItems()), 0.5, 0.5, null, null);
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
            player.openMenu(new ProjectorInventory(stack));
        } else {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(stack);
    }

    private static class ProjectorInventory extends SimpleContainer implements MenuProvider {

        private ProjectorInventory(ItemStack stack) {
            super(5);
            addListener(container -> {
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < container.getContainerSize(); i++) {
                    items.add(container.getItem(i));
                }
                stack.set(CONTENTS, ItemContainerContents.fromItems(items));
            });
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return new ProjectorContainer(id, inventory, this);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

    }

}
