package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorContainer;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorInventory;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Map;

public class ContainerProjectorTileEntity extends AbstractProjectorTileEntity implements IInventory, INamedContainerProvider, INameable {

    private final NonNullList<ItemStack> items;
    private ITextComponent name;

    public ContainerProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        items = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    public ContainerProjectorTileEntity() {
        this(TileEntityRegistry.BEAM_PROJECTOR.get());
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
        ItemStack stack = ItemStackHelper.removeItem(items, index, amount);
        if (!stack.isEmpty()) {
            markChanged();
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        setItemNoUpdate(index, stack);
        markChanged();
        setChanged();
    }

    protected void setItemNoUpdate(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return getLevel().getBlockEntity(getBlockPos()) == this && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        ITextComponent custom = getCustomName();
        if (custom != null) {
            tag.putString("CustomName", ITextComponent.Serializer.toJson(custom));
        }
        ListNBT items = new ListNBT();
        for (int i = 0; i < getContainerSize(); i++) {
            items.add(getItem(i).save(new CompoundNBT()));
        }
        tag.put("Items", items);
        return super.save(tag);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("CustomName", Constants.NBT.TAG_STRING)) {
            setCustomName(ITextComponent.Serializer.fromJson(tag.getString("CustomName")));
        }
        if (tag.contains("Items", Constants.NBT.TAG_LIST)) {
            ListNBT list = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < Math.min(tag.size(), getContainerSize()); i++) {
                ItemStack stack = ItemStack.of(list.getCompound(i));
                setItemNoUpdate(i, stack);
            }
        }
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ProjectorContainer(id, inventory, this);
    }

    @Override
    public ITextComponent getName() {
        ITextComponent custom = getCustomName();
        return custom == null ? getDefaultName() : custom;
    }

    @Override
    public ITextComponent getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return name;
    }

    public void setCustomName(ITextComponent name) {
        this.name = name;
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public Map<ProjectorModuleType, Integer> getModules() {
        return ProjectorInventory.getModuleTypes(this);
    }

}
