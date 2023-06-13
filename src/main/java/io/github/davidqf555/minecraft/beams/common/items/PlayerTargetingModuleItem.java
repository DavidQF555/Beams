package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.EntityTargetingType;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class PlayerTargetingModuleItem extends TargetingModuleItem {

    private final static Component BLACKLIST = Component.translatable("item." + Beams.ID + ".player_targeting_module.blacklist").withStyle(ChatFormatting.GREEN),
            WHITELIST = Component.translatable("item." + Beams.ID + ".player_targeting_module.whitelist").withStyle(ChatFormatting.RED),
            INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".player_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);
    private static final String PLAYER_NAME = "item." + Beams.ID + ".player_targeting_module.player_name";

    public PlayerTargetingModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public TargetingModuleType getType(ItemStack stack) {
        Predicate<Entity> condition = entity -> entity instanceof Player;
        Set<UUID> targets = getMarkedPlayers(stack).keySet();
        if (isWhitelist(stack)) {
            condition = condition.and(entity -> targets.contains(entity.getUUID()));
        } else {
            condition = condition.and(entity -> !targets.contains(entity.getUUID()));
        }
        return new EntityTargetingType(condition);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level.isClientSide() && entity instanceof Player && !getMarkedPlayers(stack).containsKey(entity.getUUID())) {
            if (player.isCrouching()) {
                removeMarkedPlayer(stack, entity.getUUID());
            } else {
                addMarkedPlayer(stack, (Player) entity);
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
        for (Component name : getMarkedPlayers(stack).values()) {
            text.add(Component.translatable(PLAYER_NAME, name).withStyle(ChatFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Map<UUID, Component> getMarkedPlayers(ItemStack stack) {
        Map<UUID, Component> players = new HashMap<>();
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Players", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Players", Tag.TAG_COMPOUND)) {
                if (((CompoundTag) nbt).contains("UUID", Tag.TAG_INT_ARRAY) && ((CompoundTag) nbt).contains("Name", Tag.TAG_STRING)) {
                    players.put(((CompoundTag) nbt).getUUID("UUID"), Component.Serializer.fromJson(((CompoundTag) nbt).getString("Name")));
                }
            }
        }
        return players;
    }

    public void addMarkedPlayer(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        ListTag list;
        if (tag.contains("Players", Tag.TAG_LIST)) {
            list = tag.getList("Players", Tag.TAG_COMPOUND);
        } else {
            list = new ListTag();
            tag.put("Players", list);
        }
        CompoundTag val = new CompoundTag();
        val.putUUID("UUID", player.getUUID());
        val.putString("Name", Component.Serializer.toJson(player.getDisplayName()));
        list.add(val);
    }

    public void removeMarkedPlayer(ItemStack stack, UUID player) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Players", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Players", Tag.TAG_COMPOUND);
            list.removeIf(nbt -> ((CompoundTag) nbt).contains("UUID", Tag.TAG_INT_ARRAY) && ((CompoundTag) nbt).getUUID("UUID").equals(player));
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
