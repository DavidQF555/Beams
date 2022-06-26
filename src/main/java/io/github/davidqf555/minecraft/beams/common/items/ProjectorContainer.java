package io.github.davidqf555.minecraft.beams.common.items;

import io.github.davidqf555.minecraft.beams.registration.ContainerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class ProjectorContainer extends Container {

    private final IInventory projector;

    public ProjectorContainer(int id, PlayerInventory player) {
        this(id, player, new Inventory(5));
    }

    public ProjectorContainer(int id, PlayerInventory player, IInventory projector) {
        super(ContainerRegistry.PROJECTOR.get(), id);
        checkContainerSize(projector, 5);
        this.projector = projector;
        projector.startOpen(player.player);
        for (int x = 0; x < 5; x++) {
            this.addSlot(new Slot(projector, x, 44 + x * 18, 20));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(player, x + y * 9 + 9, 8 + x * 18, y * 18 + 51));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(player, x, 8 + x * 18, 109));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return projector.stillValid(player);
    }
}
