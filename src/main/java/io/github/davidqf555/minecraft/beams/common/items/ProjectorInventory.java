package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProjectorInventory extends Inventory implements INamedContainerProvider {

    public ProjectorInventory() {
        super(5);
    }

    public static ProjectorInventory get(ItemStack stack) {
        return stack.getCapability(Provider.capability).orElseGet(ProjectorInventory::new);
    }

    public static Map<ProjectorModuleType, Integer> getModuleTypes(IInventory inventory) {
        Map<ProjectorModuleType, Integer> types = new HashMap<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            Item item = stack.getItem();
            if (item instanceof ProjectorModuleItem) {
                ProjectorModuleType type = ((ProjectorModuleItem<?>) item).getType();
                types.put(type, types.getOrDefault(type, 0) + stack.getCount());
            }
        }
        return types;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ProjectorContainer(id, inventory, this);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        @CapabilityInject(ProjectorInventory.class)
        private static Capability<ProjectorInventory> capability = null;
        private final LazyOptional<ProjectorInventory> instance = LazyOptional.of(() -> Objects.requireNonNull(capability.getDefaultInstance()));

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == capability ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return capability.getStorage().writeNBT(capability, instance.orElseThrow(NullPointerException::new), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            capability.getStorage().readNBT(capability, instance.orElseThrow(NullPointerException::new), null, nbt);
        }
    }

    public static class Storage implements Capability.IStorage<ProjectorInventory> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<ProjectorInventory> capability, ProjectorInventory instance, Direction side) {
            return instance.createTag();
        }

        @Override
        public void readNBT(Capability<ProjectorInventory> capability, ProjectorInventory instance, Direction side, INBT nbt) {
            instance.fromTag((ListNBT) nbt);
        }
    }
}
