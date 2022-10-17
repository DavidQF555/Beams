package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractProjectorTileEntity extends TileEntity {

    private final List<UUID> beams;

    public AbstractProjectorTileEntity(TileEntityType<?> type) {
        super(type);
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

    public void clearBeams() {
        beams.clear();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        ListNBT beams = new ListNBT();
        for (UUID beam : getBeams()) {
            beams.add(NBTUtil.createUUID(beam));
        }
        out.put("Beams", beams);
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
