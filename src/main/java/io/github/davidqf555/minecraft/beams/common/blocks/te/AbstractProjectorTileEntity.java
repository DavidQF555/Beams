package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
    private boolean changed;

    public AbstractProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        beams = new ArrayList<>();
    }

    public static <T extends AbstractProjectorTileEntity> void tick(Level level, BlockPos pos, BlockState state, T te) {
        te.tick();
    }

    protected void updateBeams() {
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (hasLevel() && block instanceof AbstractProjectorBlock) {
            clearBeams();
            if (((AbstractProjectorBlock) block).isActive(state)) {
                for (BeamEntity beam : ((AbstractProjectorBlock) block).shoot(getLevel(), getBlockPos(), state)) {
                    addBeam(beam.getUUID());
                }
            }
        }
    }

    public void clearBeams() {
        for (UUID id : new ArrayList<>(getBeams())) {
            removeBeam(id);
        }
    }

    public void tick() {
        if (changed) {
            updateBeams();
            changed = false;
        }
    }

    public void markChanged() {
        changed = true;
    }

    public List<UUID> getBeams() {
        return beams;
    }

    public void addBeam(UUID beam) {
        getBeams().add(beam);
    }

    public void removeBeam(UUID beam) {
        Level world = getLevel();
        if (world instanceof ServerLevel) {
            Entity entity = ((ServerLevel) world).getEntity(beam);
            if (entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
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
        tag.putBoolean("Changed", changed);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Beams", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Beams", Tag.TAG_INT_ARRAY)) {
                addBeam(NbtUtils.loadUUID(nbt));
            }
        }
        if (tag.contains("Changed", Tag.TAG_BYTE)) {
            changed = tag.getBoolean("Changed");
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
