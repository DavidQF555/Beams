package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.Beams;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ProjectorInventory extends SimpleContainer implements MenuProvider {

    public ProjectorInventory() {
        super(5);
    }

    public static ProjectorInventory get(ItemStack stack) {
        return stack.getCapability(Provider.CAPABILITY).orElseGet(ProjectorInventory::new);
    }

    public static Set<ProjectorModuleType> getModuleTypes(Container inventory) {
        Set<ProjectorModuleType> types = new HashSet<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            Item item = inventory.getItem(i).getItem();
            if (item instanceof ProjectorModuleItem) {
                types.add(((ProjectorModuleItem<?>) item).getType());
            }
        }
        return types;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(Util.makeDescriptionId("container", new ResourceLocation(Beams.ID, "projector")));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ProjectorContainer(id, inventory, this);
    }

    public static class Provider implements ICapabilitySerializable<ListTag> {

        public static final Capability<ProjectorInventory> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
        });
        private final LazyOptional<ProjectorInventory> instance = LazyOptional.of(ProjectorInventory::new);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public ListTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).createTag();
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            instance.orElseThrow(NullPointerException::new).fromTag(nbt);
        }
    }

}
