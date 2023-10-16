package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.EntityTargetingType;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class EntityTypeTargetingModuleItem extends TargetingModuleItem {

    private final static ITextComponent BLACKLIST = new TranslationTextComponent("item." + Beams.ID + ".entity_type_targeting_module.blacklist").withStyle(TextFormatting.GREEN),
            WHITELIST = new TranslationTextComponent("item." + Beams.ID + ".entity_type_targeting_module.whitelist").withStyle(TextFormatting.RED),
            INSTRUCTIONS = new TranslationTextComponent("item." + Beams.ID + ".entity_type_targeting_module.instructions").withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.DARK_PURPLE);
    private static final String TYPE_NAME = "item." + Beams.ID + ".entity_type_targeting_module.type_name";

    public EntityTypeTargetingModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public TargetingModuleType getType(ItemStack stack) {
        Predicate<Entity> condition;
        Set<EntityType<?>> targets = getMarkedTypes(stack);
        if (isWhitelist(stack)) {
            condition = entity -> targets.contains(entity.getType());
        } else {
            condition = entity -> !targets.contains(entity.getType());
        }
        return new EntityTargetingType(condition);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!entity.level.isClientSide() && !getMarkedTypes(stack).contains(entity.getType())) {
            if (player.isShiftKeyDown()) {
                removeMarkedType(stack, entity.getType());
            } else {
                addMarkedType(stack, entity.getType());
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        text.add(isWhitelist(stack) ? WHITELIST : BLACKLIST);
        for (EntityType<?> type : getMarkedTypes(stack)) {
            text.add(new TranslationTextComponent(TYPE_NAME, type.getDescription()).withStyle(TextFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Set<EntityType<?>> getMarkedTypes(ItemStack stack) {
        Set<EntityType<?>> types = new HashSet<>();
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Types", Constants.NBT.TAG_LIST)) {
            tag.getList("Types", Constants.NBT.TAG_STRING).stream()
                    .map(INBT::getAsString)
                    .map(ResourceLocation::new)
                    .map(ForgeRegistries.ENTITIES::getValue)
                    .forEach(types::add);
        }
        return types;
    }

    public void addMarkedType(ItemStack stack, EntityType<?> type) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        ListNBT list;
        if (tag.contains("Types", Constants.NBT.TAG_LIST)) {
            list = tag.getList("Types", Constants.NBT.TAG_STRING);
        } else {
            list = new ListNBT();
            tag.put("Types", list);
        }
        list.add(StringNBT.valueOf(type.getRegistryName().toString()));
    }

    public void removeMarkedType(ItemStack stack, EntityType<?> type) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Types", Constants.NBT.TAG_LIST)) {
            ListNBT list = tag.getList("Types", Constants.NBT.TAG_STRING);
            list.removeIf(nbt -> nbt.getAsString().equals(type.getRegistryName().toString()));
        }
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
