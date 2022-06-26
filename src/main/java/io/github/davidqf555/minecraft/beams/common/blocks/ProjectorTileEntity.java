package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorModuleItem;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

public class ProjectorTileEntity extends LockableLootTileEntity implements ITickableTileEntity {

    private final List<UUID> beams;
    private NonNullList<ItemStack> items;

    public ProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        items = NonNullList.withSize(5, ItemStack.EMPTY);
        beams = new ArrayList<>();
    }

    public ProjectorTileEntity() {
        this(TileEntityRegistry.BEAM_PROJECTOR.get());
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.getItem() instanceof ProjectorModuleItem;
    }

    @Override
    public void tick() {
        if (hasLevel()) {
            World world = getLevel();
            if (world instanceof ServerWorld && world.getGameTime() % ServerConfigs.INSTANCE.projectorUpdatePeriod.get() == 0) {
                updateBeams();
            }
        }
    }

    public void updateBeams() {
        removeBeams();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (block instanceof ProjectorBlock && state.getValue(ProjectorBlock.TRIGGERED)) {
            World world = getLevel();
            BlockPos pos = getBlockPos();
            Vector3d dir = ((ProjectorBlock) block).getBeamDirection(state);
            Vector3d start = Vector3d.atLowerCornerOf(pos).add(((ProjectorBlock) block).getStartOffset(state));
            Vector3d end = world.clip(new RayTraceContext(start, start.add(dir.scale(ServerConfigs.INSTANCE.projectorMaxRange.get())), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getLocation();
            shoot(EntityRegistry.BEAM.get(), start, end);
        }
    }

    private Set<ProjectorModuleType> getModuleTypes() {
        Set<ProjectorModuleType> types = new HashSet<>();
        for (ItemStack stack : getItems()) {
            Item item = stack.getItem();
            if (item instanceof ProjectorModuleItem) {
                types.add(((ProjectorModuleItem) item).getType());
            }
        }
        return types;
    }

    private void shoot(EntityType<BeamEntity> type, Vector3d start, Vector3d target) {
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        for (BeamEntity beam : BeamEntity.shoot(type, getLevel(), start, target, getModuleTypes(), size, size, size, size)) {
            beams.add(beam.getUUID());
        }
        setChanged();
    }

    public void removeBeams() {
        World world = getLevel();
        if (world instanceof ServerWorld) {
            for (UUID id : beams) {
                Entity entity = ((ServerWorld) world).getEntity(id);
                if (entity != null) {
                    entity.remove();
                }
            }
        }
        beams.clear();
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        ListNBT all = new ListNBT();
        for (UUID id : beams) {
            all.add(NBTUtil.createUUID(id));
        }
        out.put("Beams", all);
        if (!this.trySaveLootTable(tag)) {
            ItemStackHelper.saveAllItems(tag, this.items);
        }
        return out;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new ProjectorContainer(id, player, this);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("Beams", Constants.NBT.TAG_LIST)) {
            for (INBT nbt : tag.getList("Beams", Constants.NBT.TAG_INT_ARRAY)) {
                beams.add(NBTUtil.loadUUID(nbt));
            }
        }
        if (!tryLoadLootTable(tag)) {
            ItemStackHelper.loadAllItems(tag, items);
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        deserializeNBT(pkt.getTag());
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        updateBeams();
    }
}
