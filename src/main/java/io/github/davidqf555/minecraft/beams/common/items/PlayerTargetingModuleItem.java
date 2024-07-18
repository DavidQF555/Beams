package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTargetingModuleItem extends WhitelistTargetingModuleItem {

    private static final Component INSTRUCTIONS = new TranslatableComponent("item." + Beams.ID + ".player_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);
    private static final String PLAYER_NAME = "item." + Beams.ID + ".player_targeting_module.player_name";

    public PlayerTargetingModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean shouldTargetWhitelist(ItemStack stack, Entity entity) {
        return getMarkedPlayers(stack).containsKey(entity.getUUID());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level.isClientSide() && entity instanceof Player) {
            boolean success = player.isShiftKeyDown() ? removeMarkedPlayer(stack, entity.getUUID()) : addMarkedPlayer(stack, (Player) entity);
            return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        for (Component name : getMarkedPlayers(stack).values()) {
            text.add(new TranslatableComponent(PLAYER_NAME, name).withStyle(ChatFormatting.BLUE));
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

    public boolean addMarkedPlayer(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        ListTag list;
        if (tag.contains("Players", Tag.TAG_LIST)) {
            list = tag.getList("Players", Tag.TAG_COMPOUND);
            if (list.stream().filter(val -> ((CompoundTag) val).contains("UUID", Tag.TAG_INT_ARRAY)).map(val -> ((CompoundTag) val).getUUID("UUID")).anyMatch(player.getUUID()::equals)) {
                return false;
            }
        } else {
            list = new ListTag();
            tag.put("Players", list);
        }
        CompoundTag val = new CompoundTag();
        val.putUUID("UUID", player.getUUID());
        val.putString("Name", Component.Serializer.toJson(player.getDisplayName()));
        list.add(val);
        return true;
    }

    public boolean removeMarkedPlayer(ItemStack stack, UUID player) {
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Players", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Players", Tag.TAG_COMPOUND);
            return list.removeIf(nbt -> ((CompoundTag) nbt).contains("UUID", Tag.TAG_INT_ARRAY) && ((CompoundTag) nbt).getUUID("UUID").equals(player));
        }
        return false;
    }

}
