package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTargetingModuleItem extends WhitelistTargetingModuleItem {

    private static final ITextComponent INSTRUCTIONS = new TranslationTextComponent("item." + Beams.ID + ".player_targeting_module.instructions").withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.DARK_PURPLE);
    private static final String PLAYER_NAME = "item." + Beams.ID + ".player_targeting_module.player_name";

    public PlayerTargetingModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean shouldTargetWhitelist(ItemStack stack, Entity entity) {
        return getMarkedPlayers(stack).containsKey(entity.getUUID());
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!entity.level.isClientSide() && entity instanceof PlayerEntity) {
            boolean success = player.isShiftKeyDown() ? removeMarkedPlayer(stack, entity.getUUID()) : addMarkedPlayer(stack, (PlayerEntity) entity);
            return success ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
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

    public boolean addMarkedPlayer(ItemStack stack, PlayerEntity player) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        ListNBT list;
        if (tag.contains("Players", Constants.NBT.TAG_LIST)) {
            list = tag.getList("Players", Constants.NBT.TAG_COMPOUND);
            if (list.stream().filter(val -> ((CompoundNBT) val).contains("UUID", Constants.NBT.TAG_INT_ARRAY)).map(val -> ((CompoundNBT) val).getUUID("UUID")).anyMatch(player.getUUID()::equals)) {
                return false;
            }
        } else {
            list = new ListNBT();
            tag.put("Players", list);
        }
        CompoundNBT val = new CompoundNBT();
        val.putUUID("UUID", player.getUUID());
        val.putString("Name", ITextComponent.Serializer.toJson(player.getDisplayName()));
        list.add(val);
        return true;
    }

    public boolean removeMarkedPlayer(ItemStack stack, UUID player) {
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Players", Constants.NBT.TAG_LIST)) {
            ListNBT list = tag.getList("Players", Constants.NBT.TAG_COMPOUND);
            return list.removeIf(nbt -> ((CompoundNBT) nbt).contains("UUID", Constants.NBT.TAG_INT_ARRAY) && ((CompoundNBT) nbt).getUUID("UUID").equals(player));
        }
        return false;
    }

}
