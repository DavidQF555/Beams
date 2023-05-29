package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class PointableProjectorTileEntity extends OmnidirectionalProjectorTileEntity {

    private UUID id;

    public PointableProjectorTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        id = Mth.createInsecureUUID();
    }

    public PointableProjectorTileEntity(BlockPos pos, BlockState state) {
        this(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get(), pos, state);
    }

    public UUID getUUID() {
        return id;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID("ID", getUUID());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ID", Tag.TAG_INT_ARRAY)) {
            id = tag.getUUID("ID");
        }
    }

}
