package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectorTileEntity extends TileEntity {

    private final List<UUID> beams;

    public ProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        beams = new ArrayList<>();
    }

    public ProjectorTileEntity() {
        this(TileEntityRegistry.BEAM_PROJECTOR.get());
    }

    public void shoot(EntityType<BeamEntity> type, Vector3d start, Vector3d target, float startSize, float endSize) {
        removeBeams();
        for (BeamEntity beam : BeamEntity.shoot(type, getLevel(), start, target, startSize, startSize, endSize, endSize)) {
            beams.add(beam.getUUID());
        }
        setChanged();
    }

    public void removeBeams() {
        World world = getLevel();
        if (world instanceof ServerWorld) {
            for (UUID id : beams) {
                Entity entity = ((ServerWorld) world).getEntity(id);
                if (entity != null) {
                    entity.remove();
                }
            }
        }
        beams.clear();
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        ListNBT all = new ListNBT();
        for (UUID id : beams) {
            all.add(NBTUtil.createUUID(id));
        }
        out.put("Beams", all);
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("Beams", Constants.NBT.TAG_LIST)) {
            for (INBT nbt : tag.getList("Beams", Constants.NBT.TAG_INT_ARRAY)) {
                beams.add(NBTUtil.loadUUID(nbt));
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
