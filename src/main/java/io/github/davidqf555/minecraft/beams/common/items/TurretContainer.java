package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TurretContainer extends ProjectorContainer {

    public TurretContainer(int id, Inventory player) {
        this(id, player, new SimpleContainer(8));
    }

    public TurretContainer(int id, Inventory player, Container projector) {
        super(ContainerRegistry.TURRET.get(), id, player, projector);
    }

    @Override
    protected void initializeSlots(Inventory player) {
        for (int x = 0; x < 5; x++) {
            addSlot(new ModuleSlot(x, 8 + x * 18, 20));
        }
        for (int x = 0; x < 3; x++) {
            addSlot(new TargetingSlot(x + 5, 116 + x * 18, 20));
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

    protected class TargetingSlot extends Slot {

        protected TargetingSlot(int slot, int x, int y) {
            super(projector, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof TargetingModuleItem;
        }
    }

}
