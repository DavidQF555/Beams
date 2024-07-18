package io.github.davidqf555.minecraft.beams.common.items;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.EntityTargetingType;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;

public abstract class WhitelistTargetingModuleItem extends TargetingModuleItem {

    public static final DataComponentType<Boolean> WHITELIST = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build();
    private static final Component BLACKLIST_TEXT = Component.translatable("item." + Beams.ID + ".whitelist_targeting_module.blacklist").withStyle(ChatFormatting.GREEN);
    private static final Component WHITELIST_TEXT = Component.translatable("item." + Beams.ID + ".whitelist_targeting_module.whitelist").withStyle(ChatFormatting.RED);

    public WhitelistTargetingModuleItem(Properties properties) {
        super(properties);
    }

    protected abstract boolean shouldTargetWhitelist(ItemStack stack, Entity entity);

    @Override
    public TargetingModuleType getType(ItemStack stack) {
        Predicate<Entity> condition = entity -> shouldTargetWhitelist(stack, entity);
        if (!isWhitelist(stack)) {
            condition = condition.negate();
        }
        return new EntityTargetingType(condition);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        text.add(isWhitelist(stack) ? WHITELIST_TEXT : BLACKLIST_TEXT);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            setWhitelist(stack, !isWhitelist(stack));
            return InteractionResultHolder.success(stack);
        }
        return super.use(world, player, hand);
    }

    public void setWhitelist(ItemStack stack, boolean whitelist) {
        stack.set(WHITELIST, whitelist);
    }

    public boolean isWhitelist(ItemStack stack) {
        Boolean whitelist = stack.get(WHITELIST);
        return whitelist != null && whitelist;
    }
}
