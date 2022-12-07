package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BeamSensorTileEntity extends TileEntity {

    private final Set<UUID> hit;

    protected BeamSensorTileEntity(TileEntityType<?> type) {
        super(type);
        hit = new HashSet<>();
    }

    public BeamSensorTileEntity() {
        this(TileEntityRegistry.BEAM_SENSOR.get());
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
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        ListNBT hit = new ListNBT();
        this.hit.forEach(id -> hit.add(NBTUtil.createUUID(id)));
        out.put("Hit", hit);
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("Hit", Constants.NBT.TAG_LIST)) {
            for (INBT nbt : tag.getList("Hit", Constants.NBT.TAG_INT_ARRAY)) {
                addHit(NBTUtil.loadUUID(nbt));
            }
        }
    }
}
