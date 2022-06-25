package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectorTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int UPDATE_PERIOD = 20;
    private static final double RANGE = 16;
    private final List<UUID> beams;

    public ProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        beams = new ArrayList<>();
    }

    public ProjectorTileEntity() {
        this(TileEntityRegistry.BEAM_PROJECTOR.get());
    }

    @Override
    public void tick() {
        if (hasLevel()) {
            World world = getLevel();
            if (world instanceof ServerWorld && world.getGameTime() % UPDATE_PERIOD == 0) {
                updateBeams();
            }
        }
    }

    public void updateBeams() {
        removeBeams();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (block instanceof ProjectorBlock && state.getValue(ProjectorBlock.TRIGGERED)) {
            World world = getLevel();
            BlockPos pos = getBlockPos();
            Vector3d dir = ((ProjectorBlock) block).getBeamDirection(state);
            Vector3d start = Vector3d.atLowerCornerOf(pos).add(((ProjectorBlock) block).getStartOffset(state));
            Vector3d end = world.clip(new RayTraceContext(start, start.add(dir.scale(RANGE)), RayTraceContext.BlockMode.VISUAL, RayTraceContext.FluidMode.NONE, null)).getLocation();
            shoot(EntityRegistry.BEAM.get(), start, end);
        }
    }

    private void shoot(EntityType<BeamEntity> type, Vector3d start, Vector3d target) {
        for (BeamEntity beam : BeamEntity.shoot(type, getLevel(), start, target, 1, 1, 1, 1)) {
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
