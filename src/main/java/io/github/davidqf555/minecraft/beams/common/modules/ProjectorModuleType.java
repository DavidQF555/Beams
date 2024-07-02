package io.github.davidqf555.minecraft.beams.common.modules;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.items.ProjectorModuleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ProjectorModuleType {

    public static Map<ProjectorModuleType, Integer> getModuleTypes(Iterable<ItemStack> iterable) {
        Map<ProjectorModuleType, Integer> types = new HashMap<>();
        for (ItemStack stack : iterable) {
            Item item = stack.getItem();
            if (item instanceof ProjectorModuleItem) {
                ProjectorModuleType type = ((ProjectorModuleItem<?>) item).getType();
                types.put(type, types.getOrDefault(type, 0) + stack.getCount());
            }
        }
        return types;
    }

    public void onStart(BeamEntity beam, int amt) {

    }

    public void onEntityTick(BeamEntity beam, Entity target, int amt) {

    }

    public void onBlockTick(BeamEntity beam, BlockPos pos, int amt) {

    }

    public void onCollisionTick(BeamEntity beam, BlockPos pos, int amt) {

    }

    public double getGrowthRate(int amt) {
        return 0;
    }

    public double getStartSizeFactor(int amt) {
        return 1;
    }

    public boolean shouldTickBlocks() {
        return false;
    }

    public boolean shouldTickEntities() {
        return false;
    }

}
