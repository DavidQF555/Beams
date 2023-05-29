package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class TurretContainer extends ProjectorContainer {

    public TurretContainer(int id, PlayerInventory player) {
        this(id, player, new Inventory(8));
    }

    public TurretContainer(int id, PlayerInventory player, IInventory projector) {
        super(ContainerRegistry.TURRET.get(), id, player, projector);
    }

    @Override
    protected void initializeSlots(PlayerInventory player) {
        for (int x = 0; x < 5; x++) {
            addSlot(new ModuleSlot(projector, x, 8 + x * 18, 20));
        }
        for (int x = 0; x < 3; x++) {
            addSlot(new TargetingSlot(projector, x + 5, 116 + x * 18, 20));
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
    protected int getExpectedSize() {
        return 8;
    }

    protected static class TargetingSlot extends Slot {

        protected TargetingSlot(IInventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof TargetingModuleItem;
        }
    }

}
