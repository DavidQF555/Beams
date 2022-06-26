package io.github.davidqf555.minecraft.beams.common.blocks;

import io.github.davidqf555.minecraft.beams.common.ServerConfigs;
import io.github.davidqf555.minecraft.beams.common.entities.BeamEntity;
import io.github.davidqf555.minecraft.beams.registration.EntityRegistry;
import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectorTileEntity extends BlockEntity {

    private final List<UUID> beams;

    public ProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        beams = new ArrayList<>();
    }

    public ProjectorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.BEAM_PROJECTOR.get(), pos, state);
    }

    public void tick() {
        if (hasLevel()) {
            Level world = getLevel();
            if (world instanceof ServerLevel && world.getGameTime() % ServerConfigs.INSTANCE.projectorUpdatePeriod.get() == 0) {
                updateBeams();
            }
        }
    }

    public void updateBeams() {
        removeBeams();
        BlockState state = getBlockState();
        Block block = state.getBlock();
        if (block instanceof ProjectorBlock && state.getValue(ProjectorBlock.TRIGGERED)) {
            Level world = getLevel();
            BlockPos pos = getBlockPos();
            Vec3 dir = ((ProjectorBlock) block).getBeamDirection(state);
            Vec3 start = Vec3.atLowerCornerOf(pos).add(((ProjectorBlock) block).getStartOffset(state));
            Vec3 end = world.clip(new ClipContext(start, start.add(dir.scale(ServerConfigs.INSTANCE.projectorMaxRange.get())), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null)).getLocation();
            shoot(EntityRegistry.BEAM.get(), start, end);
        }
    }

    private void shoot(EntityType<BeamEntity> type, Vec3 start, Vec3 target) {
        double size = ServerConfigs.INSTANCE.defaultBeamSize.get();
        for (BeamEntity beam : BeamEntity.shoot(type, getLevel(), start, target, size, size, size, size)) {
            beams.add(beam.getUUID());
        }
        setChanged();
    }

    public void removeBeams() {
        Level world = getLevel();
        if (world instanceof ServerLevel) {
            for (UUID id : beams) {
                Entity entity = ((ServerLevel) world).getEntity(id);
                if (entity != null) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }
        }
        beams.clear();
        setChanged();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag all = new ListTag();
        for (UUID id : beams) {
            all.add(NbtUtils.createUUID(id));
        }
        tag.put("Beams", all);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Beams", Tag.TAG_LIST)) {
            for (Tag nbt : tag.getList("Beams", Tag.TAG_INT_ARRAY)) {
                beams.add(NbtUtils.loadUUID(nbt));
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
