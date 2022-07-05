package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorInventory;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectorTileEntity extends RandomizableContainerBlockEntity {

    private final List<UUID> beams;
    private NonNullList<ItemStack> items;

    public ProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        items = NonNullList.withSize(5, ItemStack.EMPTY);
        beams = new ArrayList<>();
    }

    public ProjectorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.BEAM_PROJECTOR.get(), pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, ProjectorTileEntity entity) {
        if (world.getGameTime() % ServerConfigs.INSTANCE.projectorUpdatePeriod.get() == 0) {
            entity.updateBeams();
        }
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public void updateBeams() {
        removeBeams();
        BlockState state = getBlockState();
        if (state.getBlock() instanceof AbstractProjectorBlock && state.getValue(AbstractProjectorBlock.TRIGGERED)) {
            shoot();
        }
    }

    private void shoot() {
        Level world = getLevel();
        BlockPos pos = getBlockPos();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        Vec3 dir = ((AbstractProjectorBlock) block).getBeamDirection(state);
        Vec3 start = Vec3.atLowerCornerOf(pos).add(((AbstractProjectorBlock) block).getStartOffset(state));
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        for (BeamEntity beam : BeamEntity.shoot(EntityRegistry.BEAM.get(), world, start, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), ProjectorInventory.getModuleTypes(this), 0.1, size, size, size, size)) {
            beams.add(beam.getUUID());
        }
        setChanged();
    }

    public void removeBeams() {
        Level world = getLevel();
        if (world instanceof ServerLevel) {
            for (UUID id : beams) {
                Entity entity = ((ServerLevel) world).getEntity(id);
                if (entity != null) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
        beams.clear();
        setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag all = new ListTag();
        for (UUID id : beams) {
            all.add(NbtUtils.createUUID(id));
        }
        tag.put("Beams", all);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new ProjectorContainer(id, player, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Beams", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Beams", Tag.TAG_INT_ARRAY)) {
                beams.add(NbtUtils.loadUUID(nbt));
            }
        }
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
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

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
