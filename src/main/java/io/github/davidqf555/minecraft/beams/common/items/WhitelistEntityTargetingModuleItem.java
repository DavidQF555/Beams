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

public abstract class WhitelistEntityTargetingModuleItem extends TargetingModuleItem {

    public static final DataComponentType<Boolean> WHITELIST_DATA = DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build();
    private final static Component BLACKLIST = Component.translatable("item." + Beams.ID + ".targeting_module.blacklist").withStyle(ChatFormatting.GREEN);
    private final static Component WHITELIST = Component.translatable("item." + Beams.ID + ".targeting_module.whitelist").withStyle(ChatFormatting.RED);

    public WhitelistEntityTargetingModuleItem(Properties properties) {
        super(properties.component(WHITELIST_DATA, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        text.add(isWhitelist(stack) ? WHITELIST : BLACKLIST);
    }

    protected abstract boolean shouldTargetWhitelist(ItemStack stack, Entity entity);

    @Override
    public TargetingModuleType getType(ItemStack stack) {
        Predicate<Entity> condition = entity -> shouldTargetWhitelist(stack, entity);
        if (!isWhitelist(stack)) {
            condition = Predicate.not(condition);
        }
        return new EntityTargetingType(condition);
    }

    public void setWhitelist(ItemStack stack, boolean whitelist) {
        stack.set(WHITELIST_DATA, whitelist);
    }

    public boolean isWhitelist(ItemStack stack) {
        Boolean val = stack.get(WHITELIST_DATA);
        return val != null && val;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            setWhitelist(stack, !isWhitelist(stack));
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

}
