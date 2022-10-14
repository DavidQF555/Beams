package io.github.davidqf555.minecraft.beams.common.blocks.te;

import com.google.common.collect.ImmutableMap;
import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.blocks.AbstractProjectorBlock;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.common.modules.ProjectorModuleType;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectorTileEntity extends TileEntity implements ITickableTileEntity {

    private final List<UUID> beams;

    public ProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        beams = new ArrayList<>();
    }

    @Override
    public void tick() {
        if (hasLevel()) {
            World world = getLevel();
            if (world instanceof ServerWorld && world.getGameTime() % ServerConfigs.INSTANCE.projectorUpdatePeriod.get() == 0) {
                setChanged();
            }
        }
    }

    protected void updateBeams() {
        removeBeams();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (block instanceof AbstractProjectorBlock && ((AbstractProjectorBlock) block).isActive(state)) {
            shoot();
        }
    }

    @Override
    public void setChanged() {
        updateBeams();
        super.setChanged();
    }

    private void shoot() {
        World world = getLevel();
        BlockPos pos = getBlockPos();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        Vector3d dir = ((AbstractProjectorBlock) block).getBeamDirection(this, state);
        Vector3d start = Vector3d.atLowerCornerOf(pos).add(((AbstractProjectorBlock) block).getStartOffset(this, state));
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        for (BeamEntity beam : BeamEntity.shoot(EntityRegistry.BEAM.get(), world, start, dir, ServerConfigs.INSTANCE.projectorMaxRange.get(), getModules(), 0.1, size, size, size, size)) {
            beams.add(beam.getUUID());
        }
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

    protected Map<ProjectorModuleType, Integer> getModules() {
        return ImmutableMap.of();
    }

}
