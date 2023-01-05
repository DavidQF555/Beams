package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractProjectorTileEntity extends TileEntity implements ITickableTileEntity {

    private final List<UUID> beams;
    private boolean changed;

    public AbstractProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        beams = new ArrayList<>();
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

    @Override
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
        World world = getLevel();
        if (world instanceof ServerWorld) {
            Entity entity = ((ServerWorld) world).getEntity(beam);
            if (entity != null) {
                entity.remove();
            }
        }
        getBeams().remove(beam);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        ListNBT beams = new ListNBT();
        for (UUID beam : getBeams()) {
            beams.add(NBTUtil.createUUID(beam));
        }
        out.put("Beams", beams);
        out.putBoolean("Changed", changed);
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("Beams", Constants.NBT.TAG_LIST)) {
            for (INBT nbt : tag.getList("Beams", Constants.NBT.TAG_INT_ARRAY)) {
                addBeam(NBTUtil.loadUUID(nbt));
            }
        }
        if (tag.contains("Changed", Constants.NBT.TAG_BYTE)) {
            changed = tag.getBoolean("Changed");
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        deserializeNBT(pkt.getTag());
    }

}
