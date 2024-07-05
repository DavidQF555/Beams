package io.github.davidqf555.minecraft.beams.common.items;

import com.mojang.serialization.Codec;
import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.IPointable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.*;

public class PointerItem extends Item {

    private static final Component CONNECTED = Component.translatable(Util.makeDescriptionId("message", ResourceLocation.fromNamespaceAndPath(Beams.ID, "pointer_connected"))).withStyle(ChatFormatting.GREEN);
    private static final Component DISCONNECTED = Component.translatable(Util.makeDescriptionId("message", ResourceLocation.fromNamespaceAndPath(Beams.ID, "pointer_disconnected"))).withStyle(ChatFormatting.RED);
    private static final String POSITION = Util.makeDescriptionId("text", ResourceLocation.fromNamespaceAndPath(Beams.ID, "position"));
    private static final Codec<Map<UUID, BlockPos>> CODEC = CompoundTag.CODEC.xmap(tag -> {
        Map<UUID, BlockPos> map = new HashMap<>();
        for (String key : tag.getAllKeys()) {
            if (tag.contains(key, Tag.TAG_INT_ARRAY)) {
                int[] val = tag.getIntArray(key);
                if (val.length != 3) {
                    continue;
                }
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException exception) {
                    continue;
                }
                map.put(id, new BlockPos(val[0], val[1], val[2]));
            }
        }
        return map;
    }, map -> {
        CompoundTag tag = new CompoundTag();
        map.forEach((id, pos) -> tag.putIntArray(id.toString(), new int[]{pos.getX(), pos.getY(), pos.getZ()}));
        return tag;
    });
    public static final DataComponentType<Map<UUID, BlockPos>> CONNECTED_LOC = DataComponentType.<Map<UUID, BlockPos>>builder().persistent(CODEC).build();

    public PointerItem(Properties properties) {
        super(properties.component(CONNECTED_LOC, new HashMap<>()));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> text, TooltipFlag flag) {
        getConnected(stack).values().stream().map(pos -> Component.translatable(POSITION, pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GREEN)).forEach(text::add);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        if (!world.isClientSide() && player != null && player.isShiftKeyDown()) {
            BlockPos pos = context.getClickedPos();
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof IPointable) {
                UUID id = ((IPointable) block).getConnectionID(world, pos);
                if (id != null) {
                    ItemStack stack = context.getItemInHand();
                    Map<UUID, BlockPos> connections = getConnected(stack);
                    if (connections.containsKey(id)) {
                        connections.remove(id);
                            player.sendSystemMessage(DISCONNECTED);
                    } else {
                        connections.put(id, pos);
                            player.sendSystemMessage(CONNECTED);
                    }
                    setConnected(stack, connections);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(hand);
            Map<UUID, BlockPos> connections = getConnected(stack);
            if (!connections.isEmpty()) {
                Vec3 start = player.getEyePosition(1);
                double range = ServerConfigs.INSTANCE.pointerRange.get();
                Vec3 end = start.add(player.getLookAngle().scale(range));
                EntityHitResult entity = ProjectileUtil.getEntityHitResult(world, player, start, end, AABB.ofSize(start, range * 2, range * 2, range * 2), check -> true);
                Vec3 target;
                if (entity == null) {
                    target = world.clip(new ClipContext(start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty())).getLocation();
                } else {
                    target = entity.getEntity().getEyePosition(1);
                }
                for (UUID key : new ArrayList<>(connections.keySet())) {
                    BlockPos pos = connections.get(key);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof IPointable && key.equals(((IPointable) block).getConnectionID(world, pos))) {
                        ((IPointable) block).onPoint(world, pos, target);
                    } else {
                        connections.remove(key);
                    }
                }
                setConnected(stack, connections);
                return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
            }
        }
        return super.use(world, player, hand);
    }

    public Map<UUID, BlockPos> getConnected(ItemStack stack) {
        Map<UUID, BlockPos> connections = stack.get(CONNECTED_LOC);
        if (connections == null) {
            connections = new HashMap<>();
            stack.set(CONNECTED_LOC, connections);
        }
        return connections;
    }

    public void setConnected(ItemStack stack, Map<UUID, BlockPos> connections) {
        stack.set(CONNECTED_LOC, connections);
    }

}
