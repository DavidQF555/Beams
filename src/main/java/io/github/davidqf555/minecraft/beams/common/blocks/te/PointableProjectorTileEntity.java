package io.github.davidqf555.minecraft.beams.common.blocks.te;

import io.github.davidqf555.minecraft.beams.registration.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class PointableProjectorTileEntity extends OmnidirectionalProjectorTileEntity {

    private UUID id;

    public PointableProjectorTileEntity(TileEntityType<?> type) {
        super(type);
        id = MathHelper.createInsecureUUID();
    }

    public PointableProjectorTileEntity() {
        this(TileEntityRegistry.OMNIDIRECTIONAL_BEAM_PROJECTOR.get());
    }

    public UUID getUUID() {
        return id;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT out = super.save(tag);
        out.putUUID("ID", getUUID());
        return out;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("ID", Constants.NBT.TAG_INT_ARRAY)) {
            id = tag.getUUID("ID");
        }
    }

}
