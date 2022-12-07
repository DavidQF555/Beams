package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BeamSensorTileEntity extends BlockEntity {

    private final Set<UUID> hit;

    protected BeamSensorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        hit = new HashSet<>();
    }

    public BeamSensorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.BEAM_SENSOR.get(), pos, state);
    }

    public boolean addHit(UUID hit) {
        return this.hit.add(hit);
    }

    public boolean removeHit(UUID hit) {
        return this.hit.remove(hit);
    }

    public Set<UUID> getHit() {
        return hit;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag hit = new ListTag();
        this.hit.forEach(id -> hit.add(NbtUtils.createUUID(id)));
        tag.put("Hit", hit);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Hit", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Hit", Tag.TAG_INT_ARRAY)) {
                addHit(NbtUtils.loadUUID(nbt));
            }
        }
    }
}
