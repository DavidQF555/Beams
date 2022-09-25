package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.DirectionalProjectorTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

public class ProjectorPointerItem extends Item {

    private static final ITextComponent CONNECTED = new TranslationTextComponent(Util.makeDescriptionId("message", new ResourceLocation(Beams.ID, "pointer_connected"))).withStyle(TextFormatting.GREEN);
    private static final ITextComponent DISCONNECTED = new TranslationTextComponent(Util.makeDescriptionId("message", new ResourceLocation(Beams.ID, "pointer_disconnected"))).withStyle(TextFormatting.RED);
    private static final String POSITION = Util.makeDescriptionId("text", new ResourceLocation(Beams.ID, "position"));

    public ProjectorPointerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        getConnected(stack).values().stream().map(pos -> new TranslationTextComponent(POSITION, pos.getX(), pos.getY(), pos.getZ()).withStyle(TextFormatting.GREEN)).forEach(text::add);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && player.isCrouching()) {
            World world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            TileEntity te = world.getBlockEntity(pos);
            if (te instanceof DirectionalProjectorTileEntity) {
                ItemStack stack = context.getItemInHand();
                Map<UUID, BlockPos> connections = getConnected(stack);
                UUID id = ((DirectionalProjectorTileEntity) te).getUUID();
                if (connections.containsKey(id)) {
                    connections.remove(id);
                    if (world.isClientSide()) {
                        player.sendMessage(DISCONNECTED, Util.NIL_UUID);
                    }
                } else {
                    connections.put(id, pos);
                    if (world.isClientSide()) {
                        player.sendMessage(CONNECTED, Util.NIL_UUID);
                    }
                }
                setConnected(stack, connections);
                return ActionResultType.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!player.isCrouching()) {
            ItemStack stack = player.getItemInHand(hand);
            Map<UUID, BlockPos> connections = getConnected(stack);
            if (!connections.isEmpty()) {
                Vector3d start = player.getEyePosition(1);
                double range = ServerConfigs.INSTANCE.pointerRange.get();
                Vector3d end = start.add(player.getLookAngle().scale(range));
                EntityRayTraceResult entity = ProjectileHelper.getEntityHitResult(world, player, start, end, AxisAlignedBB.ofSize(range * 2, range * 2, range * 2).move(start), check -> true);
                Vector3d target;
                if (entity == null) {
                    target = world.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getLocation();
                } else {
                    target = entity.getEntity().getEyePosition(1);
                }
                for (UUID key : new ArrayList<>(connections.keySet())) {
                    BlockPos pos = connections.get(key);
                    TileEntity te = world.getBlockEntity(pos);
                    if (te instanceof DirectionalProjectorTileEntity && ((DirectionalProjectorTileEntity) te).getUUID().equals(key)) {
                        Vector3d dir = target.subtract(Vector3d.atCenterOf(pos)).normalize();
                        ((DirectionalProjectorTileEntity) te).setDirection(dir);
                        te.setChanged();
                    } else {
                        connections.remove(key);
                    }
                }
                setConnected(stack, connections);
                return ActionResult.sidedSuccess(stack, world.isClientSide());
            }
        }
        return super.use(world, player, hand);
    }

    public Map<UUID, BlockPos> getConnected(ItemStack stack) {
        Map<UUID, BlockPos> connections = new HashMap<>();
        CompoundNBT tag = stack.getOrCreateTagElement(Beams.ID);
        if (tag.contains("Connections", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT map = tag.getCompound("Connections");
            for (String key : map.getAllKeys()) {
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException exception) {
                    continue;
                }
                if (map.contains(key, Constants.NBT.TAG_INT_ARRAY)) {
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
        CompoundNBT tag = new CompoundNBT();
        connections.forEach((id, pos) -> {
            tag.put(id.toString(), new IntArrayNBT(new int[]{pos.getX(), pos.getY(), pos.getZ()}));
        });
        stack.getOrCreateTagElement(Beams.ID).put("Connections", tag);
    }

}
