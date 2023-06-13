package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.EntityTargetingType;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class EntityTypeTargetingModuleItem extends TargetingModuleItem {

    private final static Component BLACKLIST = Component.translatable("item." + Beams.ID + ".entity_type_targeting_module.blacklist").withStyle(ChatFormatting.GREEN),
            WHITELIST = Component.translatable("item." + Beams.ID + ".entity_type_targeting_module.whitelist").withStyle(ChatFormatting.RED),
            INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".entity_type_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level().isClientSide() && !getMarkedTypes(stack).contains(entity.getType())) {
            if (player.isCrouching()) {
                removeMarkedType(stack, entity.getType());
            } else {
                addMarkedType(stack, entity.getType());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        text.add(isWhitelist(stack) ? WHITELIST : BLACKLIST);
        for (EntityType<?> type : getMarkedTypes(stack)) {
            text.add(Component.translatable(TYPE_NAME, type.getDescription()).withStyle(ChatFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Set<EntityType<?>> getMarkedTypes(ItemStack stack) {
        Set<EntityType<?>> types = new HashSet<>();
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Types", Tag.TAG_LIST)) {
            tag.getList("Types", Tag.TAG_STRING).stream()
                    .map(Tag::getAsString)
                    .map(ResourceLocation::new)
                    .map(ForgeRegistries.ENTITY_TYPES::getValue)
                    .forEach(types::add);
        }
        return types;
    }

    public void addMarkedType(ItemStack stack, EntityType<?> type) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        ListTag list;
        if (tag.contains("Types", Tag.TAG_LIST)) {
            list = tag.getList("Types", Tag.TAG_STRING);
        } else {
            list = new ListTag();
            tag.put("Types", list);
        }
        list.add(StringTag.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(type).toString()));
    }

    public void removeMarkedType(ItemStack stack, EntityType<?> type) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Types", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Types", Tag.TAG_STRING);
            list.removeIf(nbt -> nbt.getAsString().equals(ForgeRegistries.ENTITY_TYPES.getKey(type).toString()));
        }
    }

    public void setWhitelist(ItemStack stack, boolean whitelist) {
        stack.getOrCreateTagElement(Beams.ID).putBoolean("Whitelist", whitelist);
    }

    public boolean isWhitelist(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Whitelist", Tag.TAG_BYTE)) {
            return tag.getBoolean("Whitelist");
        }
        return false;
    }

}
