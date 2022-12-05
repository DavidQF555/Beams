package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractProjectorTileEntity extends BlockEntity {

    private final List<UUID> beams;

    public AbstractProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        beams = new ArrayList<>();
    }

    @Override
    public void setChanged() {
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (hasLevel() && block instanceof AbstractProjectorBlock) {
            ((AbstractProjectorBlock) block).updateBeam(getLevel(), getBlockPos(), state);
        }
        super.setChanged();
    }

    public List<UUID> getBeams() {
        return beams;
    }

    public void addBeam(UUID beam) {
        getBeams().add(beam);
    }

    public void removeBeam(UUID beam) {
        getBeams().remove(beam);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag beams = new ListTag();
        for (UUID beam : getBeams()) {
            beams.add(NbtUtils.createUUID(beam));
        }
        tag.put("Beams", beams);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Beams", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Beams", Tag.TAG_INT_ARRAY)) {
                addBeam(NbtUtils.loadUUID(nbt));
            }
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

}
