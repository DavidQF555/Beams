package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.blocks.RedstoneActivatedProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorInventory;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public void tick() {
        if (hasLevel()) {
            World world = getLevel();
            if (world instanceof ServerWorld && world.getGameTime() % ServerConfigs.INSTANCE.projectorUpdatePeriod.get() == 0) {
                setChanged();
            }
        }
    }

    protected void updateBeams() {
        removeBeams();
        BlockState state = getBlockState();
        if (state.getBlock() instanceof RedstoneActivatedProjectorBlock && state.getValue(RedstoneActivatedProjectorBlock.TRIGGERED)) {
            shoot();
        }
    }

    @Override
    public void setChanged() {
        updateBeams();
        super.setChanged();
    }

    private void shoot() {
        World world = getLevel();
        BlockPos pos = getBlockPos();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        Vector3d dir = ((AbstractProjectorBlock) block).getBeamDirection(this, state);
        Vector3d start = Vector3d.atLowerCornerOf(pos).add(((AbstractProjectorBlock) block).getStartOffset(this, state));
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        for (BeamEntity beam : BeamEntity.shoot(EntityRegistry.BEAM.get(), world, start, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), ProjectorInventory.getModuleTypes(this), 0.1, size, size, size, size)) {
            beams.add(beam.getUUID());
        }
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
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
