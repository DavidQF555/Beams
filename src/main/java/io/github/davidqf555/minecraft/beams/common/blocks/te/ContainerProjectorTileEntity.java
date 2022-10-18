package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorInventory;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;

public class ContainerProjectorTileEntity extends AbstractProjectorTileEntity implements Container, MenuProvider, Nameable {

    private final NonNullList<ItemStack> items;
    private Component name;

    public ContainerProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        items = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    public ContainerProjectorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.BEAM_PROJECTOR.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, index, amount);
        if (!stack.isEmpty()) {
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return getLevel().getBlockEntity(getBlockPos()) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        Component custom = getCustomName();
        if (custom != null) {
            tag.putString("CustomName", Component.Serializer.toJson(custom));
        }
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName", Tag.TAG_STRING)) {
            setCustomName(Component.Serializer.fromJson(tag.getString("CustomName")));
        }
        ContainerHelper.loadAllItems(tag, items);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ProjectorContainer(id, inventory, this);
    }

    @Override
    public Component getName() {
        Component custom = getCustomName();
        return custom == null ? getDefaultName() : custom;
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return name;
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    protected Component getDefaultName() {
        return Component.translatable(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public Map<ProjectorModuleType, Integer> getModules() {
        return ProjectorInventory.getModuleTypes(this);
    }

}
