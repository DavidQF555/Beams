package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityTypeTargetingModuleItem extends WhitelistTargetingModuleItem {

    public static final DataComponentType<Set<EntityType<?>>> MARKED_DATA = DataComponentType.<Set<EntityType<?>>>builder().persistent(ForgeRegistries.ENTITY_TYPES.getCodec().listOf().xmap(Set::copyOf, List::copyOf)).build();
    private static final Component INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".entity_type_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);
    private static final String TYPE_NAME = "item." + Beams.ID + ".entity_type_targeting_module.type_name";

    public EntityTypeTargetingModuleItem(Properties properties) {
        super(properties.component(MARKED_DATA, new HashSet<>()));
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
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        for (EntityType<?> type : getMarkedTypes(stack)) {
            text.add(Component.translatable(TYPE_NAME, type.getDescription()).withStyle(ChatFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Set<EntityType<?>> getMarkedTypes(ItemStack stack) {
        Set<EntityType<?>> types = stack.get(MARKED_DATA);
        if (types == null) {
            return Set.of();
        }
        return types;
    }

    public boolean addMarkedType(ItemStack stack, EntityType<?> type) {
        Set<EntityType<?>> types = stack.get(MARKED_DATA);
        if (types == null) {
            types = new HashSet<>();
            stack.set(MARKED_DATA, types);
        }
        return types.add(type);
    }

    public boolean removeMarkedType(ItemStack stack, EntityType<?> type) {
        Set<EntityType<?>> types = stack.get(MARKED_DATA);
        if (types != null) {
            return types.remove(type);
        }
        return false;
    }

}
