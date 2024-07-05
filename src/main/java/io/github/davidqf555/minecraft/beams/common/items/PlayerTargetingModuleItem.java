package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.client.ClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerTargetingModuleItem extends WhitelistTargetingModuleItem {

    public static final DataComponentType<Set<UUID>> MARKED = DataComponentType.<Set<UUID>>builder().persistent(UUIDUtil.CODEC_SET).build();
    private static final String PLAYER_NAME = "item." + Beams.ID + ".player_targeting_module.player_name";
    private static final Component INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".player_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);

    public PlayerTargetingModuleItem(Properties properties) {
        super(properties.component(MARKED, new HashSet<>()));
    }

    @Override
    protected boolean shouldTargetWhitelist(ItemStack stack, Entity entity) {
        return getMarkedPlayers(stack).contains(entity.getUUID());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level().isClientSide() && entity instanceof Player && !getMarkedPlayers(stack).contains(entity.getUUID())) {
            if (player.isShiftKeyDown()) {
                removeMarkedPlayer(stack, entity.getUUID());
            } else {
                addMarkedPlayer(stack, entity.getUUID());
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        for (UUID id : getMarkedPlayers(stack)) {
            Component name = ClientHelper.getDisplayName(id);
            text.add(Component.translatable(PLAYER_NAME, name == null ? Component.empty() : name).withStyle(ChatFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Set<UUID> getMarkedPlayers(ItemStack stack) {
        Set<UUID> marked = stack.get(MARKED);
        if (marked == null) {
            return Set.of();
        }
        return marked;
    }

    public void addMarkedPlayer(ItemStack stack, UUID player) {
        Set<UUID> marked = stack.get(MARKED);
        if (marked == null) {
            marked = new HashSet<>();
            stack.set(MARKED, marked);
        }
        marked.add(player);
    }

    public void removeMarkedPlayer(ItemStack stack, UUID player) {
        Set<UUID> marked = stack.get(MARKED);
        if (marked != null) {
            marked.remove(player);
        }
    }

}
