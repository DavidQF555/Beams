package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityTypeTargetingModuleItem extends WhitelistTargetingModuleItem {

    public static final DataComponentType<Set<ResourceKey<EntityType<?>>>> MARKED = DataComponentType.<Set<ResourceKey<EntityType<?>>>>builder().persistent(ResourceKey.codec(Registries.ENTITY_TYPE).listOf().xmap(Set::copyOf, List::copyOf)).build();
    private static final String TYPE_NAME = "item." + Beams.ID + ".entity_type_targeting_module.type_name";
    private final static Component INSTRUCTIONS = Component.translatable("item." + Beams.ID + ".entity_type_targeting_module.instructions").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_PURPLE);

    public EntityTypeTargetingModuleItem(Properties properties) {
        super(properties.component(MARKED, new HashSet<>()));
    }

    @Override
    protected boolean shouldTargetWhitelist(ItemStack stack, Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(entity.getType()).map(getMarkedTypes(stack)::contains).orElse(false);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (!entity.level().isClientSide()) {
            ResourceKey<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getResourceKey(entity.getType()).orElse(null);
            if (type != null && !getMarkedTypes(stack).contains(type)) {
                if (player.isShiftKeyDown()) {
                    removeMarkedType(stack, type);
                } else {
                    addMarkedType(stack, type);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, context, text, flag);
        for (ResourceKey<EntityType<?>> key : getMarkedTypes(stack)) {
            Component name = BuiltInRegistries.ENTITY_TYPE.getOptional(key).map(EntityType::getDescription).orElseGet(Component::empty);
            text.add(Component.translatable(TYPE_NAME, name).withStyle(ChatFormatting.BLUE));
        }
        text.add(INSTRUCTIONS);
    }

    public Set<ResourceKey<EntityType<?>>> getMarkedTypes(ItemStack stack) {
        Set<ResourceKey<EntityType<?>>> marked = stack.get(MARKED);
        if (marked == null) {
            return Set.of();
        }
        return marked;
    }

    public void addMarkedType(ItemStack stack, ResourceKey<EntityType<?>> type) {
        Set<ResourceKey<EntityType<?>>> marked = stack.get(MARKED);
        if (marked == null) {
            marked = new HashSet<>();
            stack.set(MARKED, marked);
        }
        marked.add(type);
    }

    public void removeMarkedType(ItemStack stack, ResourceKey<EntityType<?>> type) {
        Set<ResourceKey<EntityType<?>>> marked = stack.get(MARKED);
        if (marked != null) {
            marked.remove(type);
        }
    }

}
