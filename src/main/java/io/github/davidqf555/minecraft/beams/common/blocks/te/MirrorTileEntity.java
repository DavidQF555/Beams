package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MirrorTileEntity extends AbstractProjectorTileEntity {

    private final Set<UUID> hit;

    protected MirrorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        hit = new HashSet<>();
    }

    public MirrorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.MIRROR.get(), pos, state);
    }

    public Set<UUID> getHit() {
        return hit;
    }

    public boolean addHit(UUID hit) {
        return getHit().add(hit);
    }

    public boolean removeHit(UUID hit) {
        return getHit().remove(hit);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag hit = new ListTag();
        getHit().forEach(id -> hit.add(NbtUtils.createUUID(id)));
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
