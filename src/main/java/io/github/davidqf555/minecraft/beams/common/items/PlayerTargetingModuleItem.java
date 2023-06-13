package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.EntityTargetingType;
import io.github.davidqf555.minecraft.beams.common.modules.targeting.TargetingModuleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class PlayerTargetingModuleItem extends TargetingModuleItem {

    private final static ITextComponent BLACKLIST = new TranslationTextComponent("item." + Beams.ID + ".player_targeting_module.blacklist").withStyle(TextFormatting.GREEN),
            WHITELIST = new TranslationTextComponent("item." + Beams.ID + ".player_targeting_module.whitelist").withStyle(TextFormatting.RED),
            INSTRUCTIONS = new TranslationTextComponent("item." + Beams.ID + ".player_targeting_module.instructions").withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.DARK_PURPLE);
    private static final String PLAYER_NAME = "item." + Beams.ID + ".player_targeting_module.player_name";

    public PlayerTargetingModuleItem(Properties properties) {
        super(null, properties);
    }

    @Override
    public TargetingModuleType getType(ItemStack stack) {
        Predicate<Entity> condition = EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(entity -> entity instanceof PlayerEntity);
        Set<UUID> targets = getMarkedPlayers(stack).keySet();
        if (isWhitelist(stack)) {
            condition = condition.and(entity -> targets.contains(entity.getUUID()));
        } else {
            condition = condition.and(entity -> !targets.contains(entity.getUUID()));
        }
        return new EntityTargetingType(condition);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!entity.level.isClientSide() && entity instanceof PlayerEntity && !getMarkedPlayers(stack).containsKey(entity.getUUID())) {
            if (player.isCrouching()) {
                removeMarkedPlayer(stack, entity.getUUID());
            } else {
                addMarkedPlayer(stack, (PlayerEntity) entity);
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
        for (ITextComponent name : getMarkedPlayers(stack).values()) {
            text.add(new TranslationTextComponent(PLAYER_NAME, name).withStyle(TextFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Map<UUID, ITextComponent> getMarkedPlayers(ItemStack stack) {
        Map<UUID, ITextComponent> players = new HashMap<>();
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Players", Constants.NBT.TAG_LIST)) {
            for (INBT nbt : tag.getList("Players", Constants.NBT.TAG_COMPOUND)) {
                if (((CompoundNBT) nbt).contains("UUID", Constants.NBT.TAG_INT_ARRAY) && ((CompoundNBT) nbt).contains("Name", Constants.NBT.TAG_STRING)) {
                    players.put(((CompoundNBT) nbt).getUUID("UUID"), ITextComponent.Serializer.fromJson(((CompoundNBT) nbt).getString("Name")));
                }
            }
        }
        return players;
    }

    public void addMarkedPlayer(ItemStack stack, PlayerEntity player) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        ListNBT list;
        if (tag.contains("Players", Constants.NBT.TAG_LIST)) {
            list = tag.getList("Players", Constants.NBT.TAG_COMPOUND);
        } else {
            list = new ListNBT();
            tag.put("Players", list);
        }
        CompoundNBT val = new CompoundNBT();
        val.putUUID("UUID", player.getUUID());
        val.putString("Name", ITextComponent.Serializer.toJson(player.getDisplayName()));
        list.add(val);
    }

    public void removeMarkedPlayer(ItemStack stack, UUID player) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Players", Constants.NBT.TAG_LIST)) {
            ListNBT list = tag.getList("Players", Constants.NBT.TAG_COMPOUND);
            list.removeIf(nbt -> ((CompoundNBT) nbt).contains("UUID", Constants.NBT.TAG_INT_ARRAY) && ((CompoundNBT) nbt).getUUID("UUID").equals(player));
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
