package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.DirectionalProjectorTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectorPointerItem extends Item {

    private static final Component CONNECTED = Component.translatable(Util.makeDescriptionId("message", new ResourceLocation(Beams.ID, "pointer_connected"))).withStyle(ChatFormatting.GREEN);
    private static final Component DISCONNECTED = Component.translatable(Util.makeDescriptionId("message", new ResourceLocation(Beams.ID, "pointer_disconnected"))).withStyle(ChatFormatting.RED);

    public ProjectorPointerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isCrouching()) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof DirectionalProjectorTileEntity) {
                ItemStack stack = context.getItemInHand();
                Map<UUID, BlockPos> connections = getConnected(stack);
                UUID id = ((DirectionalProjectorTileEntity) te).getUUID();
                if (connections.containsKey(id)) {
                    connections.remove(id);
                    if (world.isClientSide()) {
                        player.sendSystemMessage(DISCONNECTED);
                    }
                } else {
                    connections.put(id, pos);
                    if (world.isClientSide()) {
                        player.sendSystemMessage(CONNECTED);
                    }
                }
                setConnected(stack, connections);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (!player.isCrouching()) {
            ItemStack stack = player.getItemInHand(hand);
            Map<UUID, BlockPos> connections = getConnected(stack);
            if (!connections.isEmpty()) {
                Vec3 start = player.getEyePosition(1);
                double range = ServerConfigs.INSTANCE.pointerRange.get();
                Vec3 end = start.add(player.getLookAngle().scale(range));
                EntityHitResult entity = ProjectileUtil.getEntityHitResult(world, player, start, end, AABB.ofSize(start, range * 2, range * 2, range * 2), check -> true);
                Vec3 target;
                if (entity == null) {
                    target = world.clip(new ClipContext(start, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null)).getLocation();
                } else {
                    target = entity.getEntity().getEyePosition(1);
                }
                for (UUID key : new ArrayList<>(connections.keySet())) {
                    BlockPos pos = connections.get(key);
                    BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof DirectionalProjectorTileEntity && ((DirectionalProjectorTileEntity) te).getUUID().equals(key)) {
                        Vec3 dir = target.subtract(Vec3.atCenterOf(pos)).normalize();
                        ((DirectionalProjectorTileEntity) te).setDirection(dir);
                        te.setChanged();
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
        Map<UUID, BlockPos> connections = new HashMap<>();
        CompoundTag tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Connections", Tag.TAG_COMPOUND)) {
            CompoundTag map = tag.getCompound("Connections");
            for (String key : map.getAllKeys()) {
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException exception) {
                    continue;
                }
                if (map.contains(key, Tag.TAG_INT_ARRAY)) {
                    int[] arr = map.getIntArray(key);
                    if (arr.length >= 3) {
                        connections.put(id, new BlockPos(arr[0], arr[1], arr[2]));
                    }
                }
            }
        }
        return connections;
    }

    public void setConnected(ItemStack stack, Map<UUID, BlockPos> connections) {
        CompoundTag tag = new CompoundTag();
        connections.forEach((id, pos) -> {
            tag.put(id.toString(), new IntArrayTag(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
        });
        stack.getOrCreateTagElement(Beams.ID).put("Connections", tag);
    }

}
