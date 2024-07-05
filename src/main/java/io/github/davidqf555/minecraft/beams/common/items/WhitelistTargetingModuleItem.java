package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.EntityTargetingType;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public abstract class WhitelistTargetingModuleItem extends TargetingModuleItem {

    private static final ITextComponent BLACKLIST = new TranslationTextComponent("item." + Beams.ID + ".whitelist_targeting_module.blacklist").withStyle(TextFormatting.GREEN);
    private static final ITextComponent WHITELIST = new TranslationTextComponent("item." + Beams.ID + ".whitelist_targeting_module.whitelist").withStyle(TextFormatting.RED);

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
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(isWhitelist(stack) ? WHITELIST : BLACKLIST);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            setWhitelist(stack, !isWhitelist(stack));
            return ActionResult.success(stack);
        }
        return ActionResult.pass(stack);
    }

    public void setWhitelist(ItemStack stack, boolean whitelist) {
        stack.getOrCreateTagElement(Beams.ID).putBoolean("Whitelist", whitelist);
    }

    public boolean isWhitelist(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Whitelist", Constants.NBT.TAG_BYTE)) {
            return tag.getBoolean("Whitelist");
        }
        return false;
    }
}
