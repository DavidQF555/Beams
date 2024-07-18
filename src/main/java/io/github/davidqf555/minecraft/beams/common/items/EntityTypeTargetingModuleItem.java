package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityTypeTargetingModuleItem extends WhitelistTargetingModuleItem {

    private static final Component INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".entity_type_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);
    private static final String TYPE_NAME = "item." + Beams.ID + ".entity_type_targeting_module.type_name";

    public EntityTypeTargetingModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean shouldTargetWhitelist(ItemStack stack, Entity entity) {
        return getMarkedTypes(stack).contains(entity.getType());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level().isClientSide()) {
            boolean success = player.isShiftKeyDown() ? removeMarkedType(stack, entity.getType()) : addMarkedType(stack, entity.getType());
            return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
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
                    .map(BuiltInRegistries.ENTITY_TYPE::get)
                    .forEach(types::add);
        }
        return types;
    }

    public boolean addMarkedType(ItemStack stack, EntityType<?> type) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        ListTag list;
        if (tag.contains("Types", Tag.TAG_LIST)) {
            list = tag.getList("Types", Tag.TAG_STRING);
            if (list.stream().map(Tag::getAsString).anyMatch(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()::equals)) {
                return false;
            }
        } else {
            list = new ListTag();
            tag.put("Types", list);
        }
        list.add(StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()));
        return true;
    }

    public boolean removeMarkedType(ItemStack stack, EntityType<?> type) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Types", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Types", Tag.TAG_STRING);
            return list.removeIf(nbt -> nbt.getAsString().equals(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()));
        }
        return false;
    }

}
