package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ProjectorContainer extends AbstractContainerMenu {

    private final Container projector;

    public ProjectorContainer(int id, Inventory player) {
        this(id, player, new SimpleContainer(5));
    }

    public ProjectorContainer(int id, Inventory player, Container projector) {
        super(ContainerRegistry.PROJECTOR.get(), id);
        checkContainerSize(projector, 5);
        this.projector = projector;
        projector.startOpen(player.player);
        for (int x = 0; x < 5; x++) {
            addSlot(new ModuleSlot(x, 44 + x * 18, 20));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(player, x + y * 9 + 9, 8 + x * 18, y * 18 + 51));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(player, x, 8 + x * 18, 109));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return projector.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            copy = stack.copy();
            if (index < projector.getContainerSize()) {
                if (!moveItemStackTo(stack, projector.getContainerSize(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, projector.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return copy;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        projector.stopOpen(player);
    }

    private static class ModuleSlot extends Slot {

        private ModuleSlot(int slot, int x, int y) {
            super(projector, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ProjectorModuleItem;
        }
    }
}
